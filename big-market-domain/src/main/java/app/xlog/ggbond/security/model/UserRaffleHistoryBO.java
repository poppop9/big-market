package app.xlog.ggbond.security.model;

import lombok.Data;

/**
 * 用户抽奖历史
 */
@Data
public class UserRaffleHistoryBO {
    private Long userId;  // 用户id
    private Long strategyId;  // 用户在哪个策略下抽奖的
    private Long awardId;  // 用户抽取到的奖品id
}
