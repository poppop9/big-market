package app.xlog.ggbond.listener;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import com.xxl.job.core.context.XxlJobHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 抽奖领域 - 监听器
 */
@Slf4j
@Component
public class RaffleListener {

    @Resource
    private IRaffleDispatchRepo raffleDispatchRepo;

    /**
     * 消费扣减奖品库存消息
     */
    @KafkaListener(topics = GlobalConstant.KafkaConstant.DECR_AWARD_INVENTORY,
            groupId = GlobalConstant.KafkaConstant.groupId)
    public void consumeMessage(ConsumerRecord<String, MQMessage<DecrQueueVO>> record) {
        DecrQueueVO decrQueueVO = record.value().getData();
        System.out.println(decrQueueVO);
        raffleDispatchRepo.updateAwardCount(decrQueueVO);
        log.info("抽奖领域 - 扣减数据库中 {} 策略 {} 奖品的库存成功", decrQueueVO.getStrategyId(), decrQueueVO.getAwardId());
    }

}
