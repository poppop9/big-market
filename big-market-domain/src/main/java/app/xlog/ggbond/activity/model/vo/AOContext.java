package app.xlog.ggbond.activity.model.vo;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeBO;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 活动单上下文
 */
@Data
@Builder
@Accessors(chain = true)
public class AOContext {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private ActivityOrderBO activityOrderBO;  // 活动单
    private ActivityOrderTypeBO activityOrderType;  // 活动单类型名称
    private Long raffleCount; // 该类型的活动单能给予的抽奖次数
    private Boolean isConditionMet;  // 是否满足条件
    private String redeemCode;  // 兑换码

    private Long AOProductId;  // 活动单商品id
    private Long purchaseQuantity;  // 购买活动单的数量
}
