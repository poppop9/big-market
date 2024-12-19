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
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    private Long strategyId;  // 绑定的策略id
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    private List<Long> awardIds; // 绑定的奖品集合

    @Enumerated(EnumType.STRING)
    private RafflePoolType rafflePoolType;  // 抽奖池类型
    private String rafflePoolName;  // 抽奖池名称
    private String ruleDescription;  // 抽奖池描述

    @Builder.Default
    private Long normalTimeStartValue = -1L;  // 普通次数抽奖池的起始值
    @Builder.Default
    private Long normalTimeEndValue = -1L;  // 普通次数抽奖池的结束值
    @Builder.Default
    private Long specialTimeValue = -1L;  // 特殊次数抽奖池的值

    @AllArgsConstructor
    @Getter
    public enum RafflePoolType {
        NormalTime("普通次数抽奖池"),  // 比如 AllAwardPool，No1stAnd2ndAwardPool ……
        SpecialTime("特殊次数抽奖池"),  // 比如 1stAnd2ndAwardPool
        SpecialRule("特殊规则抽奖池");  // 比如 BlacklistPool

        private final String desc;
    }
}
