package app.xlog.ggbond.persistent.po.reward;

import app.xlog.ggbond.persistent.po.ShardingTable;
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
        @Index(columnList = "userId"),
        @Index(columnList = "rewardAccountId, userId"),
})
public class RewardAccount extends ShardingTable {
    private Long rewardAccountId;  // 返利账户id
    private Long userId;  // 用户id
    private @Builder.Default Long points = 0L;  // 积分
}
