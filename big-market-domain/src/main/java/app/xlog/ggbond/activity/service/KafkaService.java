package app.xlog.ggbond.activity.service;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class KafkaService {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送消息
     */
    @SneakyThrows
    public void send(String topic, Object message) {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, message);
        send.whenComplete((result, ex) -> {
            if (ex != null)
                log.error("消息发送失败：{}", ex.getMessage());
            else
                log.info("消息发送成功：{}", result.getRecordMetadata());
        });


    }

    /**
     * 消费消息
     */
    @KafkaListener(topics = "test-1", groupId = "consumer_group_1")
    public void consumeMessage(ConsumerRecord<?, ?> record) {
        log.info("简单消费：{}，{}，{}，{}", record.topic(), record.partition(), record.key(), record.value());
    }

}