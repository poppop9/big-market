package app.xlog.ggbond.activity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 活动订单上下文
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrderContext {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long strategyId;  // 策略id
    private Long activityOrderId;  // 活动单id
    private LocalDateTime activityOrderEffectiveTime;  // 订单生效时间（立马生效为LocalDateTime.MIN）
    private LocalDateTime activityOrderExpireTime;  // 订单过期时间
    private ActivityOrderBO.ActivityOrderType activityOrderType;  // 订单类型
}
