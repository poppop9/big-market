package app.xlog.ggbond.raffle.model.bo;

import lombok.*;

/**
 * 用户
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBO {
    private Long userId;  // 用户id
    private boolean isBlacklistUser;  // 是否是黑名单用户
    private Long raffleTime;  // 抽奖次数
}
