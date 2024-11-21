package app.xlog.ggbond.persistent.po.raffle;

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
    @Enumerated(EnumType.STRING)
    private Long strategyId;  // 绑定的策略id
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "awardId")  // 指定Award表中的外键为awardId  todo
    private List<Award> awards; // 绑定的奖品集合
    private String rafflePoolName;  // 抽奖池名称
    private String ruleDescription;  // 抽奖池描述
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();
}
