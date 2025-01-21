package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTableBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动单类型
 * <p>
 * - 一个类型对应一种规则
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ActivityOrderType", indexes = {
        @Index(columnList = "activityOrderTypeId"),
        @Index(columnList = "activityOrderTypeName"),
})
public class ActivityOrderType extends SingleTableBaseEntity {
    private Long activityOrderTypeId;  // 活动单类型id
    private @Enumerated(EnumType.STRING) ActivityOrderTypeName activityOrderTypeName;  // 活动单类型名称（也是唯一的）

    public enum ActivityOrderTypeName {
        SIGN_IN_TO_CLAIM,  // 签到领取
        FREE_GIVEAWAY,  // 免费赠送
        PAID_PURCHASE,  // 付费购买
        REDEEM_TO_OBTAIN  // 兑换获取
    }
}
