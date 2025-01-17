package app.xlog.ggbond.persistent.po.security;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import app.xlog.ggbond.persistent.util.JpaDefaultValue;
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
        @Index(columnList = "userId"),
        @Index(columnList = "strategyId"),
        @Index(columnList = "userId, activityId"),
        @Index(columnList = "userId, strategyId")
})
public class UserRaffleConfig extends ShardingTableBaseEntity {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long strategyId;  // 用户在哪个策略下抽奖的（如果为null，默认为Activity中的defaultStrategyId）
    @Builder.Default
    private Long raffleTime = 0L;  // 抽奖次数
}
