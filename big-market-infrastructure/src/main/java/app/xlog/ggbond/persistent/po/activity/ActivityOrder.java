package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 活动单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrder", indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "activityId"),
        @Index(columnList = "userId, activityId"),
        @Index(columnList = "userId, activityId, activityOrderStatus"),
})
public class ActivityOrder extends ShardingTableBaseEntity {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private @Builder.Default Long activityOrderId = IdUtil.getSnowflakeNextId();  // 活动单id
    private Long activityOrderTypeId;  // 活动单类型id
    private @Enumerated(EnumType.STRING) ActivityOrderType.ActivityOrderTypeName activityOrderTypeName;  // 活动单类型名称
    private @Builder.Default LocalDateTime activityOrderEffectiveTime = LocalDateTime.of(2000, 12, 31, 0, 0, 0);  // 订单生效时间（立马生效为LocalDateTime.MIN）
    private @Builder.Default LocalDateTime activityOrderExpireTime = LocalDateTime.of(9999, 12, 31, 0, 0, 0);  // 订单过期时间（永久有效为LocalDateTime.MAX）
    private @Enumerated(EnumType.STRING) ActivityOrderStatus activityOrderStatus;  // 订单状态

    @AllArgsConstructor
    public enum ActivityOrderStatus {
        INITIAL("初始状态", "初始状态"),
        PENDING_PAYMENT("待支付", "创建活动单之后 --->>> 转为待支付"),
        EFFECTIVE("有效", "待支付的活动单，支付成功之后 --->>> 转为有效"),
        USED("已使用", "有效的活动单，使用之后 --->>> 转为已使用"),
        EXPIRED("已过期", "有效的活动单，过期时间到了之后 --->>> 转为已过期"),
        CLOSED("已关闭", "待支付的活动单，过期时间到了之后 --->>> 转为已关闭");

        private final String code;
        private final String info;
    }
}
