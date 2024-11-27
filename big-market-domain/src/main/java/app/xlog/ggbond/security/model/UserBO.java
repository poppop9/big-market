package app.xlog.ggbond.security.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBO {
    private Long id;
    private Long userId;  // 用户id
    private String userName;  // 用户名
    private String password;  // 密码
    private UserRole userRole;  // 用户的角色 : 0-管理员，1-普通用户，2-黑名单用户
    private Map<Long, Long> strategyRaffleTimeMap;  // 用户各个策略中的抽奖次数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Getter
    @AllArgsConstructor
    public enum UserRole {
        ADMIN(0),
        USER(1),
        BLACKLIST(2);

        private final int value;
    }
}
