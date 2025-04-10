package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.ShardingTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 活动账户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityAccount", indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "activityId")
})
@Comment("活动账户")
public class ActivityAccount extends ShardingTable {
    private @Comment("用户ID") Long userId;
    private @Comment("活动ID") Long activityId;
    private @Comment("可用的抽奖次数") Long availableRaffleCount;
    @Builder.Default
    private @Comment("余额") Double balance = 0d;
}