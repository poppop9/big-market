package app.xlog.ggbond.reward.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 兑换奖品
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangePrizesBO {
    private Long activityId;  // 活动id
    private Long exchangePrizesId;  // 兑换奖品id
    private String exchangePrizesName;  // 兑换奖品名称
    private Long points;  // 兑换该奖品所需的积分
}