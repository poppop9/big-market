package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.LongListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
public class RafflePool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createTime;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime updateTime;

    private Long strategyId;  // 绑定的策略id
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    private List<Long> awardIds; // 绑定的奖品集合

    @Enumerated(EnumType.STRING)
    private RafflePoolType rafflePoolType;  // 抽奖池类型
    private String rafflePoolName;  // 抽奖池名称
    private String ruleDescription;  // 抽奖池描述

    @Builder.Default
    private Long normalTimeStartValue = -1L;  // 普通次数抽奖池的起始值（比如No1stAnd2ndAwardPool，这里设定为0，表示抽奖大于等于0次时，才为No1stAnd2ndAwardPool）
    @Builder.Default
    private Long normalTimeEndValue = -1L;  // 普通次数抽奖池的结束值（比如No1stAnd2ndAwardPool，这里设定为9，表示抽奖小于等于9次时，才为No1stAnd2ndAwardPool）
    @Builder.Default
    private Long specialTimeValue = -1L;  // 特殊次数抽奖池的值（比如1stAnd2ndAwardPool，这里设定为50，表示抽奖第50次时，使用1stAnd2ndAwardPool）

    @AllArgsConstructor
    @Getter
    public enum RafflePoolType {
        NormalTime("普通次数抽奖池"),  // 比如 AllAwardPool，No1stAnd2ndAwardPool ……
        SpecialTime("特殊次数抽奖池"),  // 比如 1stAnd2ndAwardPool
        SpecialRule("特殊规则抽奖池");  // 比如 BlacklistPool

        private final String desc;
    }
}
