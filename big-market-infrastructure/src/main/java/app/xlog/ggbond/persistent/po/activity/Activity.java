package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Comments;

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
@Comment("活动")
public class Activity extends SingleTable {
    @Comment("活动id")
    private Long activityId;
    @Comment("活动名称")
    private String activityName;
    @Comment("积分范围（格式：\"1-10\"），随机积分奖品就可能是1-10之间的随机数")
    private String rangeOfPoints;
}