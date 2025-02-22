package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
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

import java.util.Optional;

/**
 * 有效状态 -> 已使用状态流水线
 */
@Slf4j
@LiteflowComponent
public class EffectiveToUsedPipeline {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * when - 更新过期活动单状态工位 - 更新该用户的活动单（找出过期活动单，更新状态）
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "UpdateExpiredAOStatusWorker",
            nodeName = "更新过期活动单状态工位")
    public void updateExpiredAOStatusWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        activityRepo.updateExpiredAOStatus(
                context.getActivityId(), context.getUserId()
        );
    }

    /**
     * when - 检查有效活动单工位 - 检查该用户是否有可用的活动单
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "CheckEffectiveAOWorker",
            nodeName = "检查有效活动单工位")
    public void checkEffectiveAOWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        Optional<ActivityOrderBO> effectiveActivityOrder = activityRepo.findEffectiveActivityOrder(
                context.getActivityId(), context.getUserId()
        );
        effectiveActivityOrder.ifPresentOrElse(
                item -> {
                    context.setActivityOrderBO(item);
                    context.setIsConditionMet(true);
                },
                () -> {
                    throw new BigMarketException(BigMarketRespCode.NOT_EFFECTIVE_ACTIVITY_ORDER);
                }
        );
    }

    /**
     * perform - 并发安全加锁工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "ConcurrencySafetyLockWorker",
            nodeName = "并发安全加锁工位")
    public void concurrencySafetyLockWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        if (!activityRepo.acquireConsumeAOLock(context.getUserId())) {
            throw new BigMarketException(BigMarketRespCode.USER_IS_IN_CONSUME_AO, "您当前正在消费活动单，请稍后再试");
        }
    }

    /**
     * perform - 更新活动单的抽奖次数和状态工位 - 更新活动单已使用的抽奖次数
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "UpdateAOUsedRaffleCountAndStatusWorker",
            nodeName = "更新活动单的抽奖次数和状态工位")
    public void updateAOUsedRaffleCountAndStatusWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);


        // 查出这一单的活动单对象，判断是否已经达到最大抽奖次数
        ActivityOrderBO activityOrderBO = activityRepo.findActivityOrderByActivityOrderId(context.getActivityOrderBO().getActivityOrderId());
        Long totalRaffleCount = activityOrderBO.getTotalRaffleCount();
        Long usedRaffleCount = activityOrderBO.getUsedRaffleCount();

        if (usedRaffleCount >= totalRaffleCount) {
            // 已达到最大抽奖次数，更新其状态，并且retry
            activityRepo.updateActivityOrderStatus(
                    context.getActivityOrderBO().getActivityOrderId(),
                    ActivityOrderBO.ActivityOrderStatus.USED
            );
            throw new BigMarketException(BigMarketRespCode.ACTIVITY_ORDER_IS_USED);
        } else {
            // 未达到最大抽奖次数，更新其已使用抽奖次数
            Long nowUsedRaffleCount = activityRepo.increaseAOUsedRaffleCount(
                    context.getActivityOrderBO().getActivityOrderId()
            );
            if (nowUsedRaffleCount.equals(totalRaffleCount)) {
                activityRepo.updateActivityOrderStatus(
                        context.getActivityOrderBO().getActivityOrderId(),
                        ActivityOrderBO.ActivityOrderStatus.USED
                );
            }
        }
    }

    /**
     * perform - 更新用户的可用抽奖次数工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "UpdateUserAvailableRaffleCountWorker",
            nodeName = "更新用户的可用抽奖次数工位")
    public void updateUserAvailableRaffleCountWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        activityRepo.decreaseUserAvailableRaffleCount(
                context.getActivityId(), context.getUserId()
        );
    }

    /**
     * perform - 并发安全解锁工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "ConcurrencySafetyUnLockWorker",
            nodeName = "并发安全解锁工位")
    public void concurrencySafetyUnLockWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        activityRepo.unLockUserInBitSet(context.getUserId());
    }

}
