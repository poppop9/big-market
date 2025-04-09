package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
@Comment("策略奖品关联表")
public class StrategyAward extends ShardingTable {
    private @Comment("策略ID（同1个strategyId下，一定会有9个awardId）") Long strategyId;
    private @Comment("奖品ID") Long awardId;
    private @Comment("奖品库存") Long awardCount;
    private @Comment("奖品被抽取到的概率，单位是%") Double awardRate;
    private @Comment("奖品在前端的排序") Integer awardSort;
}