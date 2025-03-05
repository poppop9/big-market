package app.xlog.ggbond.raffle.service.filterChain;

import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.activity.model.bo.ActivityBO;
import app.xlog.ggbond.activity.service.IActivityService;
import app.xlog.ggbond.exception.RetryRouterException;
import app.xlog.ggbond.raffle.model.bo.UserBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import app.xlog.ggbond.resp.BigMarketRespCode;
import app.xlog.ggbond.reward.model.PointsLogBO;
import app.xlog.ggbond.reward.service.IRewardService;
import cn.hutool.core.util.RandomUtil;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽奖领域 - 后置过滤器链
 */
@Slf4j
@LiteflowComponent
public class RaffleAfterFilters {

    @Resource
    private IRewardService rewardService;
    @Resource
    private IActivityService activityService;

    @Resource
    private IRaffleDispatchRepo raffleDispatchRepo;
    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;

    /**
     * 更新过期时间过滤器 - 更新redis中的过期时间
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "UpdateExpireTimeFilter",
            nodeName = "更新过期时间过滤器")
    public void updateExpireTimeFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);

        // 更新所有权重对象Map的过期时间
        raffleDispatchRepo.updateAllWeightRandomExpireTime2(context.getStrategyId());
        // 更新所有奖品库存的过期时间
        raffleDispatchRepo.updateAllAwardCountExpireTime(context.getStrategyId());
        // 更新所有奖品列表的过期时间
        raffleDispatchRepo.updateAllAwardListExpireTime(context.getStrategyId());
    }

    /**
     * 奖品库存过滤器 - 对库存做处理
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "AwardInventoryFilter",
            nodeName = "奖品库存过滤器")
    public void awardInventoryFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        Long userId = context.getUserBO().getUserId();
        log.atInfo().log("抽奖领域 - " + userId + " 奖品库存过滤器开始执行");

        // 调度扣减方法
        if (!raffleDispatchRepo.decreaseAwardCount(context.getStrategyId(), context.getAwardId())) {
            throw new RetryRouterException(BigMarketRespCode.DECREASE_AWARD_COUNT_FAILED, "扣减库存失败，重新调度");
        }

        // 将扣减信息写入队列
        raffleDispatchRepo.addDecrAwardCountToMQ(DecrQueueVO.builder()
                .strategyId(context.getStrategyId())
                .awardId(context.getAwardId()).build()
        );
        log.atInfo().log("抽奖领域 - " + userId + " 奖品库存过滤器执行完毕");
    }

    /**
     * 用户抽奖次数过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "UserRaffleTimeFilter",
            nodeName = "用户抽奖次数过滤器")
    public void userRaffleTimeFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        Long userId = context.getUserBO().getUserId();

        log.atInfo().log("抽奖领域 - " + userId + " 用户抽奖次数过滤器开始执行");
        raffleDispatchRepo.addUserRaffleTimeByStrategyId(userId, context.getStrategyId());
        log.atInfo().log("抽奖领域 - " + userId + " 用户抽奖次数过滤器执行完毕");
    }

    /**
     * 用户抽奖流水记录过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "UserRaffleFlowRecordFilter",
            nodeName = "用户抽奖流水记录过滤器")
    public void userRaffleFlowRecordFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        Long userId = context.getUserBO().getUserId();

        log.atInfo().log("抽奖领域 - " + userId + " 用户抽奖流水记录过滤器开始执行");
        Long userRaffleHistoryId = raffleDispatchRepo.addUserRaffleFlowRecordFilter(userId, context.getStrategyId(), context.getAwardId());
        context.setUserRaffleHistoryId(userRaffleHistoryId);
        log.atInfo().log("抽奖领域 - " + userId + " 用户抽奖流水记录过滤器执行完毕");
    }

    /**
     * 返利过滤器 todo 未测试
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "RewardFilter",
            nodeName = "返利过滤器")
    public void rewardFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        Long userId = context.getUserBO().getUserId();

        log.atInfo().log("抽奖领域 - " + userId + " 返利过滤器开始执行");
        // 1. 判断奖品是否是随机积分
        if (!context.getAwardId().equals(101L)) return;

        // 2. 查询活动积分范围
        ActivityBO activityBO = activityService.findActivityByActivityId(context.getActivityId());
        // 3. 写入积分流水表
        PointsLogBO pointsLogBO = rewardService.insetPointsLog(
                context.getActivityId(),
                userId,
                RandomUtil.randomInt(Integer.parseInt(activityBO.getRangeOfPoints().split("-")[0]), Integer.parseInt(activityBO.getRangeOfPoints().split("-")[1])),
                false
        );

        // 4. 发布积分返利的消息
        rewardService.publishPointsRewardMessage(MQMessage.<PointsLogBO>builder()
                .data(pointsLogBO)
                .build()
        );
        log.atInfo().log("抽奖领域 - " + userId + " 返利过滤器执行完毕");
    }

}
