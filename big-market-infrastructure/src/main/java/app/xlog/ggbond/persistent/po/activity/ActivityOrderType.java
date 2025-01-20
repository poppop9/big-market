package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动单的类型
 * <p>
 * - 一个活动下会有多个ActivityOrderType
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderType", indexes = {
        // @Index(columnList = "activityId")
})
public class ActivityOrderType extends SingleTableBaseEntity {
    private Long activityId;  // 活动id
    private Long activityOrderTypeId;  // 活动单类型id
    private String activityOrderTypeName;  // 活动单类型名称
    private Long raffleCount; // 该类型的活动单能给予的抽奖次数

    /*
        SIGN_IN_TO_CLAIM,  // 签到领取
        FREE_GIVEAWAY,  // 免费赠送
        PAID_PURCHASE,  // 付费购买
        REDEEM_TO_OBTAIN  // 兑换获取
    */
}
