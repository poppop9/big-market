package app.xlog.ggbond.persistent.po.reward;

import app.xlog.ggbond.persistent.po.ShardingTable;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 返利账户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RewardAccount", indexes = {
        @Index(columnList = "rewardAccountId"),
        @Index(columnList = "activityId"),
        @Index(columnList = "userId"),
        @Index(columnList = "activityId, userId"),
        @Index(columnList = "rewardAccountId, userId"),
})
@Comment("返利账户")
public class RewardAccount extends ShardingTable {
    @Builder.Default
    private @Comment("返利账户ID") Long rewardAccountId = IdUtil.getSnowflakeNextId();
    private @Comment("活动ID") Long activityId;
    private @Comment("用户ID") Long userId;
    @Builder.Default
    private @Comment("积分数量，默认为0") Long points = 0L;
}