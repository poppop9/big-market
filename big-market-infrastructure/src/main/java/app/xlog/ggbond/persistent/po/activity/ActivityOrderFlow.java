package app.xlog.ggbond.persistent.po.activity;

import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动单流水
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderFlow", indexes = {
        // @Index(columnList = "userId")
})
public class ActivityOrderFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createTime;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime updateTime;

    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long strategyId;  // 策略id
    @Builder.Default
    private Long activityOrderId = IdUtil.getSnowflakeNextId();  // 活动单id
    @Builder.Default
    private LocalDateTime activityOrderEffectiveTime = LocalDateTime.MIN;  // 订单生效时间（立马生效为LocalDateTime.MIN）
    @Builder.Default
    private LocalDateTime activityOrderExpireTime = LocalDateTime.MAX;  // 订单过期时间（永久有效为LocalDateTime.MAX）
    private ActivityOrderType activityOrderType;  // 订单类型
    private ActivityOrderStatus activityOrderStatus;  // 订单状态

    public enum ActivityOrderType {
        SIGN_IN_TO_CLAIM,  // 签到领取
        FREE_GIVEAWAY,  // 免费赠送
        PAID_PURCHASE,  // 付费购买
        REDEEM_TO_OBTAIN  // 兑换获取
    }

    public enum ActivityOrderStatus {
        INITIAL,  // 初始状态
        NOT_USED,  // 未使用
        USED,  // 已使用
        EXPIRED,  // 已失效
    }
}
