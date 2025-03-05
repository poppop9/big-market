package app.xlog.ggbond.reward.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返利账户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardAccountBO {
    private Long rewardAccountId;  // 返利账户id
    private Long activityId;  // 活动id
    private Long userId;  // 用户id
    private Long points;  // 积分
}
