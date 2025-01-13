package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.SingleTableBaseEntity;
import app.xlog.ggbond.persistent.util.JpaDefaultValue;
import app.xlog.ggbond.persistent.util.LongListToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
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
public class Activity extends SingleTableBaseEntity {
    private Long activityId;  // 活动id
    private Long defaultStrategyId;  // 默认策略id（每个活动都会有一个默认的策略id）
    @Column(columnDefinition = "TEXT")
    @Convert(converter = LongListToJsonConverter.class)
    @Builder.Default
    private List<Long> strategyIdList = Collections.emptyList();  // 该活动下的所有的策略id（不同活动的策略id也不能重复）
}
