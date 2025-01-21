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
    private ActivityOrderType.ActivityOrderTypeName activityOrderTypeName;  // 活动单类型名称
    private @Builder.Default LocalDateTime activityOrderEffectiveTime = LocalDateTime.of(2000, 12, 31, 0, 0, 0);  // 订单生效时间（立马生效为LocalDateTime.MIN）
    private @Builder.Default LocalDateTime activityOrderExpireTime = LocalDateTime.of(9999, 12, 31, 0, 0, 0);  // 订单过期时间（永久有效为LocalDateTime.MAX）
    private @Enumerated(EnumType.STRING) ActivityOrderStatus activityOrderStatus;  // 订单状态

    @AllArgsConstructor
    public enum ActivityOrderStatus {
        INITIAL("初始状态"),
        EFFECTIVE("有效"),
        USED("已使用"),
        EXPIRED("已过期");

        private final String message;
    }
}
