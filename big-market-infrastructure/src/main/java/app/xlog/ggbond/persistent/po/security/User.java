package app.xlog.ggbond.persistent.po.security;

import app.xlog.ggbond.persistent.po.ShardingTable;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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
@Comment("用户")
public class User extends ShardingTable {
    @Builder.Default
    private @Comment("用户id") Long userId = IdUtil.getSnowflakeNextId();
    private @Comment("用户名") String userName;
    private @Comment("密码") String password;
    @Enumerated(EnumType.STRING)
    private @Comment("用户角色") UserRole userRole;

    @Getter
    @AllArgsConstructor
    public enum UserRole {
        ADMIN(0),
        USER(1),
        BLACKLIST(2);  // 黑名单用户不根据活动区分，一黑则黑

        private final int value;
    }
}
