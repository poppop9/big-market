package app.xlog.ggbond.activity.model.bo;

import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动兑换码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRedeemCodeBO {
    private Long activityId;  // 活动id
    private String redeemCode = IdUtil.randomUUID();  // 兑换码
    private Long raffleCount;  // 该兑换码能兑换的抽奖次数
    private Boolean isUsed = false;  // 是否已使用
    private Long userId;  // 使用者id
}