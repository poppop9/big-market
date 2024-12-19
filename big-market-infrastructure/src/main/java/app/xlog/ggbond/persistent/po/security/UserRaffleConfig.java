package app.xlog.ggbond.persistent.po.security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户抽奖配置
 * <p>
 * - 这个表是以策略id为单位的，一个策略id一条数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "UserRaffleConfig", indexes = {
        @Index(columnList = "userId, activityId"),
        @Index(columnList = "userId, strategyId")
})
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
    private Long strategyId;  // 用户在哪个策略下抽奖的（如果为null，默认为Activity中的defaultStrategyId）
    @Builder.Default
    private Long raffleTime = 0L;  // 抽奖次数
}
