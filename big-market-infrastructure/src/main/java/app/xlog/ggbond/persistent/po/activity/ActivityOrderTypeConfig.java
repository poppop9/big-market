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
 * 活动单类型配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderType", indexes = {
        @Index(columnList = "activityId"),
        @Index(columnList = "activityOrderTypeId"),
        @Index(columnList = "activityOrderTypeName"),
})
public class ActivityOrderTypeConfig extends SingleTableBaseEntity {
    private Long activityId;  // 活动id
    private Long activityOrderTypeId;  // 活动单类型id
    private ActivityOrderType.ActivityOrderTypeName activityOrderTypeName;  // 活动单类型名称
    private Long raffleCount; // 该类型的活动单能给予的抽奖次数
}
