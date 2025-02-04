package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeConfigBO;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

/**
 * 待支付状态 -> 有效状态流水线
 */
@Slf4j
@LiteflowComponent
public class PendingPaymentToEffectivePipeline {

    @Resource
    private ThreadPoolTaskScheduler myScheduledThreadPool;

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
                .filter(item -> item.getActivityOrderTypeName().equals(context.getActivityOrderType().getActivityOrderTypeName()))
                .findFirst()
                .get();

        // 2. 并准备好下一个工位所需的数据
        if (nodeId.getRaffleCount() != -1L) {
            context.setRaffleCount(nodeId.getRaffleCount());
            context.getActivityOrderBO().setTotalRaffleCount(nodeId.getRaffleCount());
        }
        context.setActivityOrderType(ActivityOrderTypeBO.builder()
                .activityOrderTypeId(nodeId.getActivityOrderTypeId())
                .activityOrderTypeName(nodeId.getActivityOrderTypeName())
                .build()
        );

        // 3. 判断传入的规则是否在所有规则中，如果没有则报错，如果有则返回对应工位的nodeId
        context.getActivityOrderType().setActivityOrderTypeId(nodeId.getActivityOrderTypeId());
        context.getActivityOrderBO().setActivityOrderTypeId(nodeId.getActivityOrderTypeId());
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

        // todo 查看传入的上下文信息，判断用户的余额是否充足
        context.setIsConditionMet(true);
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
