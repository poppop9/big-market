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
public class RewardAccount extends ShardingTable {
    private @Builder.Default Long rewardAccountId = IdUtil.getSnowflakeNextId();  // 返利账户id
    private Long activityId;  // 活动id
    private Long userId;  // 用户id
    private @Builder.Default Long points = 0L;  // 积分
}
