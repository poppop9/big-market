package app.xlog.ggbond.activity.model.po;

import cn.hutool.core.util.IdUtil;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 活动单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ActivityOrderBO {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private @Builder.Default Long activityOrderId = IdUtil.getSnowflakeNextId();  // 活动单id
    private Long activityOrderTypeId;  // 活动单类型id
    private ActivityOrderTypeBO.ActivityOrderTypeName activityOrderTypeName;  // 活动单类型名称
    private @Builder.Default LocalDateTime activityOrderEffectiveTime = LocalDateTime.of(2000, 12, 31, 0, 0, 0);  // 订单生效时间（立马生效为LocalDateTime.MIN）
    private @Builder.Default LocalDateTime activityOrderExpireTime = LocalDateTime.of(9999, 12, 31, 0, 0, 0);  // 订单过期时间（永久有效为LocalDateTime.MAX）
    private ActivityOrderStatus activityOrderStatus;  // 订单状态

    /**
     * 活动单状态
     */
    @Getter
    @AllArgsConstructor
    public enum ActivityOrderStatus {
        INITIAL("初始状态"),
        EFFECTIVE("有效"),
        USED("已使用"),
        EXPIRED("已过期");

        private final String message;
    }

    /**
     * 活动单事件
     */
    @Getter
    @AllArgsConstructor
    public enum ActivityOrderEvents {
        CreateActivityOrder("创建活动订单"),
        PAYING( "支付确认"),
        PAY_SUCCESS( "支付成功"),
        PAY_FAIL( "支付失败");

        private final String message;
    }
}
