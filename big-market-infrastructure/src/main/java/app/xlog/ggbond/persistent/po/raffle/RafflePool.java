package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import app.xlog.ggbond.persistent.util.LongListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 抽奖池
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RafflePool", indexes = {
        @Index(columnList = "strategyId")
})
public class RafflePool extends ShardingTable {
    private Long strategyId;  // 绑定的策略id
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    private List<Long> awardIds; // 绑定的奖品集合

    private @Enumerated(EnumType.STRING) RafflePoolType rafflePoolType;  // 抽奖池类型
    private String rafflePoolName;  // 抽奖池名称
    private String ruleDescription;  // 抽奖池描述

    private @Builder.Default Long normalTimeStartValue = -1L;  // 普通次数抽奖池的起始值（比如No1stAnd2ndAwardPool，这里设定为0，表示抽奖大于等于0次时，才为No1stAnd2ndAwardPool）
    private @Builder.Default Long normalTimeEndValue= -1L;  // 普通次数抽奖池的结束值（比如No1stAnd2ndAwardPool，这里设定为9，表示抽奖小于等于9次时，才为No1stAnd2ndAwardPool）
    private @Builder.Default Long specialTimeValue= -1L;  // 特殊次数抽奖池的值（比如1stAnd2ndAwardPool，这里设定为50，表示抽奖第50次时，使用1stAnd2ndAwardPool）

    @AllArgsConstructor
    @Getter
    public enum RafflePoolType {
        NormalTime("普通次数抽奖池"),  // 比如 AllAwardPool，No1stAnd2ndAwardPool ……
        SpecialTime("特殊次数抽奖池"),  // 比如 1stAnd2ndAwardPool
        SpecialRule("特殊规则抽奖池");  // 比如 BlacklistPool

        private final String desc;
    }
}
