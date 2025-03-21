package app.xlog.ggbond.listener;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.service.IActivityService;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

/**
 * 活动领域 - 监听器
 */
@Slf4j
@Component
public class ActivityListener {

    @Resource
    private FlowExecutor flowExecutor;
    @Resource
    private IActivityService activityService;

    /**
     * 消费发放有效活动单任务消息
     */
    @KafkaListener(topics = GlobalConstant.KafkaConstant.REWARD_EFFECTIVE_ACTIVITY_ORDER_TASK,
            groupId = GlobalConstant.KafkaConstant.GROUP_ID,
            errorHandler = "ignoreErrorHandler")
    @SneakyThrows
    public void consumeIssuanceEffectiveAOMessage(ConsumerRecord<String, MQMessage<AOContext>> record) {
        // 执行待支付状态 -> 有效状态活动单生成链
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp(
                "PENDING_PAYMENT_TO_EFFECTIVE",
                null,
                record.value().getData()
        );
        if (!liteflowResponse.isSuccess()) throw liteflowResponse.getCause();

        // 更新 task
        activityService.updateActivityOrderIssuanceTaskStatus(
                record.value().getData().getActivityOrderRewardTaskId(), true
        );
        log.debug("活动领域 - 发放有效活动单任务消息消费成功 {}", record.value().getMessageId());
    }

}
