package app.xlog.ggbond.raffle.model.bo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 抽奖池业务对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RafflePoolBO {
    private Long id;
    private Long strategyId;  // 绑定的策略id
    private List<Long> awardIds; // 绑定的奖品集合
    private String rafflePoolName;  // 抽奖池名称
}
