package app.xlog.ggbond.raffle.model.bo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 抽奖池业务对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RafflePoolBO {
    private Long strategyId;  // 绑定的策略id
    private Long awardId;  // 绑定的奖品id
    private String rafflePoolName;  // 抽奖池名称
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();
}
