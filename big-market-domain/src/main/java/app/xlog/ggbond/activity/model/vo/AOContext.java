package app.xlog.ggbond.activity.model.vo;

import app.xlog.ggbond.activity.model.po.ActivityOrderTypeBO;
import lombok.Builder;
import lombok.Data;

/**
 * 活动单上下文
 */
@Data
@Builder
public class AOContext {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private ActivityOrderTypeBO activityOrderType;  // 活动单类型名称
    private Long raffleCount; // 该类型的活动单能给予的抽奖次数
    private Boolean isConditionMet;  // 是否满足条件
}
