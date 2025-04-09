package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 策略
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Strategy", indexes = {
        @Index(columnList = "activityId"),
        @Index(columnList = "strategyId"),
        @Index(columnList = "activityId, strategyId"),
})
@Comment("策略")
public class Strategy extends ShardingTable {
    private @Comment("活动ID") Long activityId;
    @Column(unique = true)
    @Builder.Default
    private @Comment("策略ID，全局唯一（不同活动的策略ID也不能重复）") Long strategyId = IdUtil.getSnowflakeNextId();
    private @Comment("策略描述") String strategyDesc;
}
