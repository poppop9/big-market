package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
