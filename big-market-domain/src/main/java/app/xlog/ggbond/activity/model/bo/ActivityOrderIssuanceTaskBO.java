package app.xlog.ggbond.activity.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动单发放任务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityOrderIssuanceTaskBO {
    private Long activityOrderIssuanceTaskId;  // 奖品发放任务id
    private Boolean isIssued = false;  // 奖品是否发放

    // 一些必要数据
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long activityOrderId;  // 活动单id
    private Long activityOrderTypeId;  // 活动单类型id
    private Long raffleCount;  // 该类型的活动单能给予的抽奖次数
}