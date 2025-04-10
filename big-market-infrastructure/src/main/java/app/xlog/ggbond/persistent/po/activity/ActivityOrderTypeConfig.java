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

/**
 * 活动单类型配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderTypeConfig", indexes = {
        @Index(columnList = "activityId"),
        @Index(columnList = "activityOrderTypeId"),
        @Index(columnList = "activityOrderTypeName"),
})
@Comment("活动单类型配置")
public class ActivityOrderTypeConfig extends SingleTable {
    private @Comment("活动ID") Long activityId;
    private @Comment("活动单类型ID") Long activityOrderTypeId;
    private @Comment("活动单类型名称") ActivityOrderType.ActivityOrderTypeName activityOrderTypeName;
    private @Comment("该类型的活动单能给予的抽奖次数（-1表示给予的抽奖次数不固定）") Long raffleCount;
}