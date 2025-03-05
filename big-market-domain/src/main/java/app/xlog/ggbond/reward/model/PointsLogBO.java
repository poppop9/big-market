package app.xlog.ggbond.reward.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 积分流水表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsLogBO {
    private Long pointsLogId;  // 积分流水id
    private Long activityId;  // 活动id
    private Long userId;  // 用户id
    private Long points;  // 积分
    private Boolean isIssued;  // 是否发放
}
