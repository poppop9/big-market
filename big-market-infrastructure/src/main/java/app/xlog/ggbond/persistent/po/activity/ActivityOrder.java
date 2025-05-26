package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.ShardingTable;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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
@Comment("活动单")
public class ActivityOrder extends ShardingTable {
    private @Comment("用户ID") Long userId;
    private @Comment("活动ID") Long activityId;
    @Builder.Default
    private @Comment("活动单ID") Long activityOrderId = IdUtil.getSnowflakeNextId();
    private @Comment("活动单类型ID") Long activityOrderTypeId;
    @Enumerated(EnumType.STRING)
    private @Comment("活动单类型名称") ActivityOrderType.ActivityOrderTypeName activityOrderTypeName;
    private @Comment("活动单商品ID(如果不是付费购买的，则为空)") Long activityOrderProductId;
    @Builder.Default
    private @Comment("该活动单已使用的抽奖次数") Long usedRaffleCount = 0L;
    private @Comment("该活动单购买拥有的总抽奖次数") Long totalRaffleCount;
    @Builder.Default
    private @Comment("订单生效时间（立马生效为LocalDateTime.MIN）") LocalDateTime activityOrderEffectiveTime = LocalDateTime.of(2000, 12, 31, 0, 0, 0);
    @Builder.Default
    private @Comment("订单过期时间（永久有效为LocalDateTime.MAX）") LocalDateTime activityOrderExpireTime = LocalDateTime.of(9999, 12, 31, 0, 0, 0);
    @Enumerated(EnumType.STRING)
    private @Comment("订单状态") ActivityOrderStatus activityOrderStatus;

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
