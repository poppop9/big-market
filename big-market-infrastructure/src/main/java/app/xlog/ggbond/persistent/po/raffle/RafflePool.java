package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.LongListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽奖池
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RafflePool")
public class RafflePool {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long strategyId;  // 绑定的策略id
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    private List<Long> awardIds; // 绑定的奖品集合
    private String rafflePoolName;  // 抽奖池名称
    private String ruleDescription;  // 抽奖池描述
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();
}
