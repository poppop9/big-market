package app.xlog.ggbond.job;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateAwardCount {

    @Resource
    private IRaffleDispatchRepo raffleDispatchRepo;

    /**
     * 定时循环扣减队列信息，更新数据库中的奖品库存
     */
    @Async("myAsyncExecutorThreadPool")
    @Scheduled(initialDelay = 5000, fixedDelay = 50)
    public void exec() {
        DecrQueueVO decrQueueVO = raffleDispatchRepo.queryDecrAwardCountFromQueue();

        if (decrQueueVO != null) {
            raffleDispatchRepo.updateAwardCount(decrQueueVO);
            log.atInfo().log("抽奖领域 - 定时任务 - 扣减数据库中 {} 策略 {} 奖品的库存成功", decrQueueVO.getStrategyId(), decrQueueVO.getAwardId());
        }
        log.atInfo().log("测试" + Thread.currentThread().getName());
    }

}