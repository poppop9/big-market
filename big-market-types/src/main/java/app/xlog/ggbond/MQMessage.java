package app.xlog.ggbond;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * kafka 消息数据
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MQMessage<T> {
    private @Builder.Default Long messageId = IdUtil.getSnowflakeNextId();  // 消息id
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")  // 用于处理泛型
    private T data;  // 消息数据
}
