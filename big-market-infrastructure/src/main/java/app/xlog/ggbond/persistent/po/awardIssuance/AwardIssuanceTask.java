package app.xlog.ggbond.persistent.po.awardIssuance;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import app.xlog.ggbond.persistent.po.raffle.UserRaffleHistory;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品发放任务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "AwardIssuanceTask", indexes = {
        @Index(columnList = "awardIssuanceId"),
        @Index(columnList = "userId"),
})
public class AwardIssuanceTask extends ShardingTableBaseEntity {
    private @Builder.Default Long awardIssuanceId = IdUtil.getSnowflakeNextId();  // 奖品发放任务id
    private Long userId;  // 用户id
    private Long userRaffleHistoryId;  // 用户抽奖历史id
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private @Builder.Default Boolean isIssued = false;  // 奖品是否发放
}
