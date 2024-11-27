package app.xlog.ggbond.persistent.po.security;

import app.xlog.ggbond.persistent.po.LongListToJsonConverter;
import app.xlog.ggbond.persistent.po.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;  // 用户id
    private String userName;  // 用户名
    private String password;  // 密码
    private UserRole userRole;  // 用户的角色 : 0-管理员，1-普通用户，2-黑名单用户
    @Builder.Default
    @Column(columnDefinition = "TEXT")
    @Convert(converter = MapToJsonConverter.class)
    private Map<Long, Long> strategyRaffleTimeMap = new HashMap<>();  // 用户各个策略中的抽奖次数
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    @Getter
    @AllArgsConstructor
    public enum UserRole {
        ADMIN(0),
        USER(1),
        BLACKLIST(2);

        private final int value;
    }
}
