package app.xlog.ggbond.mq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

/**
 * Kafka 配置
 */
@Configuration
public class KafkaConfig {

    /**
     * 忽略错误
     */
    @Bean
    public KafkaListenerErrorHandler ignoreErrorHandler() {
        return (message, exception) -> {
            System.err.println("忽略处理错误: " + exception);
            return null;  // 返回 null 或任何需要的默认值
        };
    }

}