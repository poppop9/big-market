package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTable;
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
public class Activity extends SingleTable {
    private Long activityId;  // 活动id
    private String activityName;  // 活动名称
    private String rangeOfPoints; // 积分范围（格式："1-10"），随机积分奖品就可能是1-10之间的随机数
}