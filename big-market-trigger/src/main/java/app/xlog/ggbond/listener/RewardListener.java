package app.xlog.ggbond.listener;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.reward.model.RewardTaskBO;
import app.xlog.ggbond.reward.service.IRewardService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 返利领域 - 监听器
 */
@Slf4j
@Component
public class RewardListener {

    @Resource
    private IRewardService rewardService;

    /**
     * 消费返利任务消息，进行发奖，并更新task表
     */
    @KafkaListener(topics = GlobalConstant.KafkaConstant.REWARD_TASK,
            groupId = GlobalConstant.KafkaConstant.GROUP_ID)
    public void consumeAwardIssuanceMessage(ConsumerRecord<String, MQMessage<RewardTaskBO>> record) {
        RewardTaskBO data = record.value().getData();

        // 1. 发奖 todo

        // 2. 更新task表
        rewardService.updateRewardTaskStatus(
                data.getRewardId(), true
        );
    }

}
