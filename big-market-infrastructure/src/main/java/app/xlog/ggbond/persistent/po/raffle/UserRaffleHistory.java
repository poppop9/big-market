package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户抽奖历史
 * <p>
 * - 由于通常需要根据userId查询，所以也会选择userId作为分片键
 * - 安全领域主要关注用户的个人信息及其与抽奖的关系，所以放置在安全领域
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "UserRaffleHistory", indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "userId, strategyId"),
})
public class UserRaffleHistory  extends ShardingTable {
    private Long userId;  // 用户id
    private Long strategyId;  // 用户在哪个策略下抽奖的
    private Long awardId;  // 用户抽取到的奖品id（表数据以奖品id为单位）
}
