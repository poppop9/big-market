package app.xlog.ggbond.activity.model.vo;

import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.bo.ActivityOrderTypeBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 活动单上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AOContext {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private ActivityOrderBO activityOrderBO;  // 活动单
    private ActivityOrderTypeBO activityOrderType;  // 活动单类型名称
    private Long raffleCount; // 该类型的活动单能给予的抽奖次数
    private Boolean isConditionMet;  // 是否满足条件
    private String redeemCode;  // 兑换码

    private Long aoProductId;  // 活动单商品id
    private Long purchaseQuantity;  // 购买活动单的数量

    private Long activityOrderRewardTaskId;  // 活动单发放任务id
}
