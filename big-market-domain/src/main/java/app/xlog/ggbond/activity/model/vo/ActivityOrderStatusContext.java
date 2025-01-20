package app.xlog.ggbond.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 活动单状态上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrderStatusContext {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long activityOrderId;  // 活动单id
    private LocalDateTime activityOrderEffectiveTime;  // 订单生效时间（立马生效为LocalDateTime.MIN）
    private LocalDateTime activityOrderExpireTime;  // 订单过期时间
}
