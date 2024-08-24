package app.xlog.ggbond.job;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UpdateAwardCount {
    private static final Logger log = LoggerFactory.getLogger(UpdateAwardCount.class);
    @Resource
    private IRaffleRepository raffleRepository;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        log.info("定时任务开始执行 - 将队列中的扣减信息取出");
        DecrQueueVO decrQueueVO = raffleRepository.queryDecrAwardCountFromQueue();

        if (decrQueueVO != null) {
            log.atInfo().log("定时任务 - 开始扣减数据库中 {} 策略 {} 奖品的库存", decrQueueVO.getStrategyId(), decrQueueVO.getAwardId());
            raffleRepository.updateAwardCount(decrQueueVO);

            log.atInfo().log("定时任务 - 扣减成功");
        }

        log.atInfo().log("定时任务 - 队列中无扣减信息");
    }
}