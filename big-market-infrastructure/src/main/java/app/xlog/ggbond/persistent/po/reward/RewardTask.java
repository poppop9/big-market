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
@Comment("返利任务")
public class RewardTask extends ShardingTable {
    @Builder.Default
    private @Comment("返利任务ID") Long rewardId = IdUtil.getSnowflakeNextId();
    private @Comment("用户ID") Long userId;
    private @Comment("用户抽奖历史ID") Long userRaffleHistoryId;
    @Builder.Default
    private @Comment("奖品是否发放，默认为false") Boolean isIssued = false;
}