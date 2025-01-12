package app.xlog.ggbond.mq;

import app.xlog.ggbond.MQMessage;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * kafka 事件中心
 */
@Slf4j
@Component
public class KafkaEventCenter implements MQEventCenter {

    @Resource
    private KafkaTemplate<String, MQMessage<?>> kafkaTemplate;

    /**
     * 发送消息
     */
    @Override
    @SneakyThrows
    public boolean sendMessage(String topic, MQMessage<?> MQMessage) {
        CompletableFuture<SendResult<String, MQMessage<?>>> send = kafkaTemplate.send(topic, MQMessage);
        CompletableFuture<Boolean> handle = send.handle((result, ex) -> {
            if (ex != null) {
                log.error("消息发送失败：{}", ex.getMessage());
                return false;
            } else {
                log.debug("消息发送成功：{}", result.getProducerRecord().value());
                return true;
            }
        });

        return handle.get();
    }

}
