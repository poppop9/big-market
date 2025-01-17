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
    private String activityName;  // 活动名称
}
