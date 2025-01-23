package app.xlog.ggbond.activity.model.po;

import lombok.Builder;
import lombok.Data;

/**
 * 活动单类型
 */
@Data
@Builder
public class ActivityOrderTypeBO {
    private Long activityOrderTypeId;  // 活动单类型id
    private ActivityOrderTypeName activityOrderTypeName;  // 活动单类型名称（也是唯一的）

    public enum ActivityOrderTypeName {
        SIGN_IN_TO_CLAIM,  // 签到领取
        PAID_PURCHASE,  // 付费购买
        REDEEM_TO_OBTAIN  // 兑换获取
    }
}