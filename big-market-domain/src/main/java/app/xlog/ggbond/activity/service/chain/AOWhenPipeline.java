package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.BigMarketException;
import app.xlog.ggbond.BigMarketRespCode;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeConfigBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.activity.service.statusFlow.AOEventCenter;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 活动单条件判断的流水线
 */
@Slf4j
@LiteflowComponent
public class AOWhenPipeline {

    @Resource
    private ThreadPoolTaskScheduler myScheduledThreadPool;

    @Resource
    private IActivityRepo activityRepo;

    /**
     * 活动单类型路由器 - 跟据活动单类型路由到相应的判断条件去
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
        if (nodeId.getRaffleCount() != -1L) context.setRaffleCount(nodeId.getRaffleCount());
        context.setActivityOrderType(ActivityOrderTypeBO.builder()
                .activityOrderTypeId(nodeId.getActivityOrderTypeId())
                .activityOrderTypeName(nodeId.getActivityOrderTypeName())
                .build()
        );

        // 3. 判断传入的规则是否在所有规则中，如果没有则报错，如果有则返回对应工位的nodeId
        return nodeId.getActivityOrderTypeName().toString();
    }

    /**
     * 签到领取裁判 - 判断是否满足签到领取的条件
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
     * 付费购买裁判 - 判断是否满足付费购买的条件
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
     * 兑换获取裁判 - 判断是否满足兑换获取的条件
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
            // 异步更新兑换码的使用状态
            CompletableFuture.runAsync(
                    () -> {
                        activityRepo.updateActivityRedeemCodeIsUsed(
                                context.getUserId(),
                                context.getRedeemCode()
                        );
                    },
                    myScheduledThreadPool
            );
        } else {
            throw new BigMarketException(BigMarketRespCode.ACTIVITY_REDEEM_CODE_ERROR);
        }
    }

}
