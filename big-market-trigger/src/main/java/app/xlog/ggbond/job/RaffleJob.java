package app.xlog.ggbond.job;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 抽奖领域 - 抽奖领域任务
 */
@Slf4j
@Component
public class RaffleJob {

    @Resource
    private IRaffleDispatchRepo raffleDispatchRepo;

    /**
     * 已废弃（已被kafka代替） - 扣减队列信息，更新数据库中的奖品库存
     */
    @Deprecated
    @XxlJob("updateAwardCount")
    public void updateAwardCount() {
        DecrQueueVO decrQueueVO = raffleDispatchRepo.queryDecrAwardCountFromQueue();
        if (decrQueueVO != null) {
            raffleDispatchRepo.updateAwardCount(decrQueueVO);
            XxlJobHelper.log("抽奖领域 - 扣减数据库中 {} 策略 {} 奖品的库存成功", decrQueueVO.getStrategyId(), decrQueueVO.getAwardId());
        } else {
            XxlJobHelper.log("抽奖领域 - 队列中没有需要扣减的奖品库存");
        }
    }

}