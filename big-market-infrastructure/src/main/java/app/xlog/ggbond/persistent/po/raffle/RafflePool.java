package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import app.xlog.ggbond.persistent.util.LongListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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
@Comment("抽奖池")
public class RafflePool extends ShardingTable {
    private @Comment("绑定的策略ID") Long strategyId;
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    private @Comment("绑定的奖品集合") List<Long> awardIds;

    @Enumerated(EnumType.STRING)
    private @Comment("抽奖池类型") RafflePoolType rafflePoolType;
    private @Comment("抽奖池名称") String rafflePoolName;
    private @Comment("抽奖池描述") String ruleDescription;

    @Builder.Default
    @Comment("普通次数抽奖池的起始值（比如No1stAnd2ndAwardPool，这里设定为0，表示抽奖大于等于0次时，才为No1stAnd2ndAwardPool）")
    private Long normalTimeStartValue = -1L;
    @Builder.Default
    @Comment("普通次数抽奖池的结束值（比如No1stAnd2ndAwardPool，这里设定为9，表示抽奖小于等于9次时，才为No1stAnd2ndAwardPool）")
    private Long normalTimeEndValue = -1L;
    @Builder.Default
    @Comment("特殊次数抽奖池的起始值（比如1stAnd2ndAwardPool，这里设定为10，表示抽奖大于等于10次时，才为1stAnd2ndAwardPool）")
    private Long specialTimeValue = -1L;

    @AllArgsConstructor
    @Getter
    public enum RafflePoolType {
        NormalTime("普通次数抽奖池"),  // 比如 AllAwardPool，No1stAnd2ndAwardPool ……
        SpecialTime("特殊次数抽奖池"),  // 比如 1stAnd2ndAwardPool
        SpecialRule("特殊规则抽奖池");  // 比如 BlacklistPool

        private final String desc;
    }
}
