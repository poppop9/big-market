package app.xlog.ggbond.activity.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动账户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityAccountBO {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long availableRaffleCount;  // 可用的抽奖次数
    private Double balance;  // 余额
    private Long points;  // 积分
}