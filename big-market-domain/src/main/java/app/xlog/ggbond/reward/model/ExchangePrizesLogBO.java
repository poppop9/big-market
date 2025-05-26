package app.xlog.ggbond.reward.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 兑换奖品流水
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangePrizesLogBO {
    private Long activityId;  // 活动id
    private Long userId;  // 用户id
    private Long exchangePrizesId;  // 兑换奖品id
    private String exchangePrizesName;
    private LocalDateTime createTime;
}
