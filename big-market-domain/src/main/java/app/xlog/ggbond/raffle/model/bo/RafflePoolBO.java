package app.xlog.ggbond.raffle.model.bo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 抽奖池
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RafflePoolBO {
    private Long id;
    private Long strategyId;  // 绑定的策略id
    private List<Long> awardIds; // 绑定的奖品集合
    private RafflePoolType rafflePoolType;  // 抽奖池类型
    private String rafflePoolName;  // 抽奖池名称

    private Long normalTimeStartValue;  // 普通次数抽奖池的起始值
    private Long normalTimeEndValue;  // 普通次数抽奖池的结束值
    private Long specialTimeValue;  // 特殊次数抽奖池的值

    @AllArgsConstructor
    @Getter
    public enum RafflePoolType {
        NormalTime("普通次数抽奖池"),  // 比如 AllAwardPool，No1stAnd2ndAwardPool ……
        SpecialTime("特殊次数抽奖池"),  // 比如 1stAnd2ndAwardPool
        SpecialRule("特殊规则抽奖池");  // 比如 BlacklistPool

        private final String desc;
    }
}
