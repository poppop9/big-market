package app.xlog.ggbond.persistent.po.security;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.*;

/**
 * 用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User", indexes = {
        @Index(columnList = "userRole"),
        @Index(columnList = "userId"),
        @Index(columnList = "userId, password"),
})
public class User extends ShardingTableBaseEntity {
    @Builder.Default
    private Long userId = IdUtil.getSnowflakeNextId();  // 用户id
    private String userName;  // 用户名
    private String password;  // 密码
    @Enumerated(EnumType.STRING)
    private UserRole userRole;  // 用户的角色

    @Getter
    @AllArgsConstructor
    public enum UserRole {
        ADMIN(0),
        USER(1),
        BLACKLIST(2);  // 黑名单用户不根据活动区分，一黑则黑

        private final int value;
    }

}
