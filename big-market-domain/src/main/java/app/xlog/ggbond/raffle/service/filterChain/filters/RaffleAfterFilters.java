package app.xlog.ggbond.raffle.service.filterChain.filters;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.model.vo.RetryRouterException;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 后置过滤器链
 */
@Slf4j
@LiteflowComponent
public class RaffleAfterFilters {

    @Resource
    private IRaffleDispatchRepo raffleDispatchRepo;

    /**
     * 奖品库存过滤器 - 对库存做处理
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "AwardInventoryFilter",
            nodeName = "奖品库存过滤器")
    public void awardInventoryFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        log.atInfo().log("抽奖领域 - " + context.getUserId() + " 奖品库存过滤器开始执行");

        // 调度扣减方法
        if (!raffleDispatchRepo.decreaseAwardCount(context.getStrategyId(), context.getAwardId())) {
            throw new RetryRouterException("扣减库存失败，重新调度");
        }

        // 将扣减信息写入队列
        raffleDispatchRepo.addDecrAwardCountToQueue(DecrQueueVO.builder()
                .strategyId(context.getStrategyId())
                .awardId(context.getAwardId())
                .build()
        );
        log.atInfo().log("抽奖领域 - " + context.getUserId() + " 奖品库存过滤器执行完毕");
    }

    /**
     * 用户抽奖次数过滤器
     * - 不能延迟处理，因为用户可能马上要进行第二次抽奖，这就会导致数据不一致
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "UserRaffleTimeFilter",
            nodeName = "用户抽奖次数过滤器")
    public void userRaffleTimeFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        log.atInfo().log("抽奖领域 - " + context.getUserId() + " 用户抽奖次数过滤器开始执行");

        // 如果是游客，就不要增加抽奖次数
        if (context.getUserId() == null) {
            log.atInfo().log("抽奖领域 - " + context.getUserId() + " 用户抽奖次数过滤器执行完毕");
            return;
        }

        raffleDispatchRepo.addUserRaffleTimeByStrategyId(context.getUserId(), context.getStrategyId());
        log.atInfo().log("抽奖领域 - " + context.getUserId() + " 用户抽奖次数过滤器执行完毕");
    }

}
