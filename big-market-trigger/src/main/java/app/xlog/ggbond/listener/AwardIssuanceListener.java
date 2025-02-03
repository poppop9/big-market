package app.xlog.ggbond.listener;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.awardIssuance.model.AwardIssuanceTaskBO;
import app.xlog.ggbond.awardIssuance.service.IAwardIssuanceService;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 奖品发放领域 - 监听器
 */
@Slf4j
@Component
public class AwardIssuanceListener {

    @Resource
    private IAwardIssuanceService awardIssuanceService;

    /**
     * 消费奖品发放任务消息，进行发奖，并更新task表
     */
    @KafkaListener(topics = GlobalConstant.KafkaConstant.AWARD_ISSUANCE_TASK,
            groupId = GlobalConstant.KafkaConstant.GROUP_ID)
    public void consumeAwardIssuanceMessage(ConsumerRecord<String, MQMessage<AwardIssuanceTaskBO>> record) {
        AwardIssuanceTaskBO data = record.value().getData();

        // 1. 发奖 todo

        // 2. 更新task表
        awardIssuanceService.updateAwardIssuanceTaskStatus(
                data.getAwardIssuanceId(), true
        );
    }

}
