package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
@Comment("用户抽奖配置")
public class UserRaffleConfig extends ShardingTable {
    private @Comment("用户ID") Long userId;
    private @Comment("活动ID") Long activityId;
    private @Comment("用户在哪个策略下抽奖的") Long strategyId;
    @Builder.Default
    private @Comment("抽奖次数") Long raffleTime = 0L;
}
