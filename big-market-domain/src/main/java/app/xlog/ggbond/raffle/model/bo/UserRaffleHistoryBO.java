package app.xlog.ggbond.raffle.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户抽奖历史
 */
@Data
public class UserRaffleHistoryBO {
    private Long userId;  // 用户id
    private Long strategyId;  // 用户在哪个策略下抽奖的
    private Long awardId;  // 用户抽取到的奖品id
    private String awardTitle;  // 奖品名称
    private LocalDateTime createTime;
}
