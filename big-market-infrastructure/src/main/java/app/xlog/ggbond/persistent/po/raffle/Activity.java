package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.LongListToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    private Long activityId;  // 活动id
    private Long defaultStrategyId;  // 默认策略id（每个活动都会有一个默认的策略id）
    @Builder.Default
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    private List<Long> strategyIdList = new ArrayList<>();  // 该活动下的所有的策略id（不同活动的策略id也不能重复）
}
