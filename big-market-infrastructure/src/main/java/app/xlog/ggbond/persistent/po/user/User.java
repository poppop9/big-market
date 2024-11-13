package app.xlog.ggbond.persistent.po.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;  // 用户id
    private String userName;  // 用户名
    private Long userRole;  // 用户的角色 : 0-管理员，1-普通用户，2-黑名单用户
}
