package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.LongListToJsonConverter;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Activity", indexes = {
        @Index(columnList = "activityId")
})
public class Activity {
    @Id
    @Builder.Default
    private Long id = IdUtil.getSnowflakeNextId();  // todo 主键的雪花算法最好使用shardingSphere配置
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createTime;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime updateTime;

    private Long activityId;  // 活动id
    private Long defaultStrategyId;  // 默认策略id（每个活动都会有一个默认的策略id）
    @Builder.Default
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    private List<Long> strategyIdList = new ArrayList<>();  // 该活动下的所有的策略id（不同活动的策略id也不能重复）
}
