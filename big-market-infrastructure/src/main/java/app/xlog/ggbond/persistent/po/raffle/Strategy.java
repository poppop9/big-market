package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Strategy extends ShardingTable {
    private Long activityId;  // 活动id
    @Column(unique = true)
    private @Builder.Default Long strategyId = IdUtil.getSnowflakeNextId();  // 策略id，全局唯一（不同活动的策略id也不能重复）
    private String strategyDesc;  // 策略描述
}
