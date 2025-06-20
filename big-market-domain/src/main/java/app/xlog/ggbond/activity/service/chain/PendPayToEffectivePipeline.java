package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.bo.*;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.resp.BigMarketRespCode;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 待支付状态 -> 有效状态流水线
 */
@Slf4j
@LiteflowComponent
public class PendPayToEffectivePipeline {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * when - 活动单类型路由器 - 跟据活动单类型路由到相应的判断条件去
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.SWITCH,
            value = LiteFlowMethodEnum.PROCESS_SWITCH,
            nodeId = "ActivityOrderTypeRouter",
            nodeName = "活动单类型路由器")
    public String ActivityOrderTypeRouter(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        // 1. 从上下文中拿出活动id，查询该活动id的所有规则
        List<ActivityOrderTypeConfigBO> configList = activityRepo.findActivityOrderTypeConfigByActivityId(context.getActivityId());
        ActivityOrderTypeConfigBO nodeId = configList.stream()
                .filter(item -> item
                        .getActivityOrderTypeName()
                        .equals(context.getActivityOrderType().getActivityOrderTypeName()))
                .findFirst()
                .get();

        // 2. 并准备好下一个工位所需的数据
        if (nodeId.getRaffleCount() != -1L) {
            context.setRaffleCount(nodeId.getRaffleCount());
            context.getActivityOrderBO()
                    .setTotalRaffleCount(nodeId.getRaffleCount())
                    .setActivityOrderTypeId(nodeId.getActivityOrderTypeId());
        }
        context.setActivityOrderType(ActivityOrderTypeBO.builder()
                .activityOrderTypeId(nodeId.getActivityOrderTypeId())
                .activityOrderTypeName(nodeId.getActivityOrderTypeName())
                .build()
        );

        // 3. 返回对应工位的nodeId
        return nodeId.getActivityOrderTypeName().toString();
    }

    /**
     * when - 签到领取裁判 - 判断是否满足签到领取的条件
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "SIGN_IN_TO_CLAIM",
            nodeName = "签到领取裁判")
    public void signInToClaimJudge(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        // 判断今天是否有领取记录，如果没有，则满足条件
        boolean isSignIn = activityRepo.existSignInToClaimAOToday(context.getUserId(), context.getActivityId());
        context.setIsConditionMet(!isSignIn);
        if (isSignIn) throw new BigMarketException(BigMarketRespCode.ACTIVITY_SIGN_IN_TO_CLAIM_AO_EXIST);

        // 活动单有效期只限今天
        context.setActivityOrderBO(context.getActivityOrderBO()
                .setActivityOrderExpireTime(LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
        );
    }

    /**
     * when - 付费购买裁判 - 判断是否满足付费购买的条件
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "PAID_PURCHASE",
            nodeName = "付费购买裁判")
    public void paidPurchaseJudge(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        // 1. 计算出用户需要购买的商品总价
        ActivityOrderProductBO activityOrderProductBO = activityRepo.findAOProductByAOProductId(
                context.getAoProductId()
        );
        if (activityOrderProductBO == null)
            throw new BigMarketException(BigMarketRespCode.ACTIVITY_ORDER_PRODUCT_NOT_EXIST);
        Double totalPrice = activityOrderProductBO.getActivityOrderProductPrice() * context.getPurchaseQuantity();

        // 2. 查询用户的余额
        ActivityAccountBO activityAccountBO = activityRepo.findActivityAccountByUserIdAndActivityId(context.getUserId(), context.getActivityId());
        Double balance = activityAccountBO.getBalance();

        // 3. 判断用户的余额是否充足
        if (balance < totalPrice) {
            throw new BigMarketException(BigMarketRespCode.ACTIVITY_BALANCE_NOT_ENOUGH);
        } else {
            // 4. 更新用户的余额
            activityRepo.updateActivityAccountBalanceByUserIdAndActivityId(
                    balance - totalPrice, context.getUserId(), context.getActivityId()
            );
            context.setRaffleCount(activityOrderProductBO.getPurchasingPower() * context.getPurchaseQuantity());
            context.setIsConditionMet(true);
        }

        // 4. 设置活动单有效期永久
        context.setActivityOrderBO(context.getActivityOrderBO()
                .setActivityOrderExpireTime(GlobalConstant.validLocalDateTimeMax)
        );
    }

    /**
     * when - 兑换获取裁判 - 判断是否满足兑换获取的条件
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "REDEEM_TO_OBTAIN",
            nodeName = "兑换获取裁判")
    public void redeemToObtainJudge(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        // 判断兑换码是否有效
        boolean isExist = activityRepo.existRedeemCode(
                context.getActivityId(), context.getRedeemCode()
        );
        context.setIsConditionMet(isExist);
        if (isExist) {
            Long raffleCount = activityRepo.findActivityRedeemCodeByRedeemCode(context.getRedeemCode()).getRaffleCount();
            context.setRaffleCount(raffleCount);
            context.getActivityOrderBO().setTotalRaffleCount(raffleCount);
            // 异步更新兑换码的使用状态
            activityRepo.updateActivityRedeemCodeIsUsed(
                    context.getUserId(), context.getRedeemCode()
            );
        } else {
            throw new BigMarketException(BigMarketRespCode.ACTIVITY_REDEEM_CODE_ERROR);
        }

        // 设置活动单有效期永久
        context.setActivityOrderBO(context.getActivityOrderBO()
                .setActivityOrderExpireTime(GlobalConstant.validLocalDateTimeMax)
        );
    }

    /**
     * perform - 待支付状态转为有效状态工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "PendingPaymentToEffectiveWorker",
            nodeName = "待支付状态转为有效状态工位")
    public void pendingPaymentToEffectiveWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        activityRepo.updateActivityOrderStatusAndAOTypeIdAndTotalRaffleCount(
                context.getActivityOrderBO().getActivityOrderId(),
                ActivityOrderBO.ActivityOrderStatus.EFFECTIVE,
                context.getActivityOrderType().getActivityOrderTypeId(),
                context.getRaffleCount()
        );
        activityRepo.updateAOExpireTime(
                context.getActivityOrderBO().getActivityOrderId(),
                context.getActivityOrderBO().getActivityOrderExpireTime()
        );
    }

    /**
     * perform - 增加可用抽奖次数工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "IncreaseAvailableRaffleTimeWorker",
            nodeName = "增加可用抽奖次数工位")
    public void increaseAvailableRaffleTimeWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);
        activityRepo.increaseAvailableRaffleTime(
                context.getUserId(), context.getActivityId(), context.getRaffleCount()
        );
    }

}
