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
 * 返利任务
 * <p>
 * - 针对于除了随机积分的其他奖品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RewardTask", indexes = {
        @Index(columnList = "rewardId"),
        @Index(columnList = "userId"),
})
public class RewardTask extends ShardingTable {
    private @Builder.Default Long rewardId = IdUtil.getSnowflakeNextId();  // 返利任务id
    private Long userId;  // 用户id
    private Long userRaffleHistoryId;  // 用户抽奖历史id
    private @Builder.Default Boolean isIssued = false;  // 奖品是否发放
}