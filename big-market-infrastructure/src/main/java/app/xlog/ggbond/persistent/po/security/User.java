package app.xlog.ggbond.persistent.po.security;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.persistent.po.MapToJsonConverter;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
    @Builder.Default
    private Long userId = IdUtil.getSnowflakeNextId();  // 用户id
    private String userName;  // 用户名
    private String password;  // 密码
    @Enumerated(EnumType.STRING)
    private UserRole userRole;  // 用户的角色
/*    @Builder.Default
    @Column(columnDefinition = "TEXT")
    @Convert(converter = MapToJsonConverter.class)
    private LinkedHashMap<Long, Long> strategyRaffleTimeMap = new LinkedHashMap<>(Map.of(GlobalConstant.defaultStrategyId, 0L));  // 用户各个策略中的抽奖次数*/
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
