package app.xlog.ggbond.raffle.model.bo;

import lombok.Data;

/**
 * 用户抽奖配置
 */
@Data
public class UserRaffleConfigBO {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long strategyId;  // 用户在哪个策略下抽奖的（所有活动的默认都有相同的策略id，如果后续生成了，就覆盖）
    private Long raffleTime;  // 抽奖次数 todo 过滤器链好像少了加这个吧
}
