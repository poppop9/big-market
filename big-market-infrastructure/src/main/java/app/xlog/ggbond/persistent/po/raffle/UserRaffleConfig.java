package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.GlobalConstant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户抽奖配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "UserRaffleConfig")
public class UserRaffleConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    @Builder.Default
    private Long strategyId = GlobalConstant.defaultStrategyId;  // 用户在哪个策略下抽奖的（所有活动的默认都有相同的策略id，如果后续生成了，就覆盖）
    private Long raffleTime;  // 抽奖次数
}
