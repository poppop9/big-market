package app.xlog.ggbond.activity.model.po;

import lombok.Data;

/**
 * 活动单类型
 */
@Data
public class ActivityOrderTypeBO {
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