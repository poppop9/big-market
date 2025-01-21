package app.xlog.ggbond.activity.model.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动单类型配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrderTypeConfigBO {
    private Long activityId;  // 活动id
    private Long activityOrderTypeId;  // 活动单类型id
    private ActivityOrderTypeBO.ActivityOrderTypeName activityOrderTypeName;  // 活动单类型名称
    private Long raffleCount; // 该类型的活动单能给予的抽奖次数
}
