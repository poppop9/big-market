package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
@Comment("活动单类型")
public class ActivityOrderType extends SingleTable {
    private @Comment("活动单类型ID") Long activityOrderTypeId;
    @Enumerated(EnumType.STRING)
    private @Comment("活动单类型名称（也是唯一的）") ActivityOrderTypeName activityOrderTypeName;

    public enum ActivityOrderTypeName {
        SIGN_IN_TO_CLAIM,  // 签到领取
        PAID_PURCHASE,  // 付费购买
        REDEEM_TO_OBTAIN  // 兑换获取
    }
}