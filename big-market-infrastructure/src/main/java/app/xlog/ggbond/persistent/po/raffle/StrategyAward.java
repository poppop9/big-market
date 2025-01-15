package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略·奖品中间表
 * <p>
 * - 多对多关系
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "StrategyAward", indexes = {
        @Index(columnList = "strategyId"),
        @Index(columnList = "awardId"),
        @Index(columnList = "strategyId, awardId")
})
public class StrategyAward extends ShardingTableBaseEntity {
    private Long strategyId;  // 策略id（同1个strategyId下，一定会有9个awardId）
    private Long awardId;  // 奖品id
    private Long awardCount;  // 奖品库存
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    private Integer awardSort;  // 奖品在前端的排序
}
