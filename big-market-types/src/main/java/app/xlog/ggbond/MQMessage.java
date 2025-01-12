package app.xlog.ggbond;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * kafka 消息数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MQMessage<T> {
    @Builder.Default
    private Long messageId = IdUtil.getSnowflakeNextId();  // 消息id
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")  // 用于处理泛型
    private T data;  // 消息数据
}
