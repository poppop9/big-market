package app.xlog.ggbond.raffle.model.bo;

import lombok.Builder;
import lombok.Getter;

/**
 * 用户
 */
@Builder
@Getter
public class UserBO {
    private Long userId;  // 用户id
    private boolean isBlacklistUser;  // 是否是黑名单用户
    private Long raffleTime;  // 抽奖次数
}