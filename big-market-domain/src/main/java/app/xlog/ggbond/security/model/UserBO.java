package app.xlog.ggbond.security.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 安全领域 - 用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBO {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Long userId;  // 用户id
    private String userName;  // 用户名
    private String password;  // 密码
    private UserRole userRole;  // 用户的角色 : 0-管理员，1-普通用户，2-黑名单用户

    @Getter
    @AllArgsConstructor
    public enum UserRole {
        ADMIN(0),
        USER(1),
        BLACKLIST(2);

        private final int value;
    }
}
