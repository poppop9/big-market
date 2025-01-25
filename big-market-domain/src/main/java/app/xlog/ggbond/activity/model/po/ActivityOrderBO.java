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
    private @Builder.Default Long usedRaffleCount = 0L; // 该活动单已使用的抽奖次数
    private Long totalRaffleCount;  // 该活动单购买拥有的总抽奖次数
    private @Builder.Default LocalDateTime activityOrderEffectiveTime = LocalDateTime.of(2000, 12, 31, 0, 0, 0);  // 订单生效时间（立马生效为LocalDateTime.MIN）
    private @Builder.Default LocalDateTime activityOrderExpireTime = LocalDateTime.of(9999, 12, 31, 0, 0, 0);  // 订单过期时间（永久有效为LocalDateTime.MAX）
    private ActivityOrderStatus activityOrderStatus;  // 订单状态

    /**
     * 活动单状态
     */
    @Getter
    @AllArgsConstructor
    public enum ActivityOrderStatus {
        INITIAL("初始", "初始"),
        PENDING_PAYMENT("待支付", "创建活动单之后 --->>> 转为待支付"),
        EFFECTIVE("有效", "待支付的活动单，支付成功之后 --->>> 转为有效"),
        USED("已使用", "有效的活动单，使用之后 --->>> 转为已使用"),
        EXPIRED("已过期", "有效的活动单，过期时间到了之后 --->>> 转为已过期"),
        CLOSED("已关闭", "待支付的活动单，过期时间到了之后 --->>> 转为已关闭");

        private final String code;
        private final String info;
    }

    /**
     * 活动单事件
     */
    @Getter
    @AllArgsConstructor
    public enum ActivityOrderEvent {
        INITIAL_TO_PENDING_PAYMENT("初始状态 --->>> 待支付状态"),
        PENDING_PAYMENT_TO_EFFECTIVE("待支付状态 --->>> 有效状态"),
        EFFECTIVE_TO_USED("有效状态 --->>> 已使用状态"),
        PAY_FAIL("支付失败");

        private final String message;
    }
}
