package app.xlog.ggbond.job;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateAwardCount {

    @Resource
    private IAwardInventoryRepository awardInventoryRepository;

    /**
     * 定时循环扣减队列信息，更新数据库中的奖品库存  todo 临时注释
     */
//    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void exec() {
        DecrQueueVO decrQueueVO = awardInventoryRepository.queryDecrAwardCountFromQueue();

        if (decrQueueVO != null) {
            log.atInfo().log("抽奖领域 - 定时任务 - 开始扣减数据库中 {} 策略 {} 奖品的库存", decrQueueVO.getStrategyId(), decrQueueVO.getAwardId());
            awardInventoryRepository.updateAwardCount(decrQueueVO);

            log.atInfo().log("抽奖领域 - 定时任务 - 扣减成功");
        }

        log.atInfo().log("抽奖领域 - 定时任务 - 队列中无扣减信息");
    }
}