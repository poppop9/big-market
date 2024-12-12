package app.xlog.ggbond.persistent.po.security;

import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户抽奖历史
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "UserRaffleHistory")
public class UserRaffleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;  // 用户id
    private Long strategyId;  // 用户在哪个策略下抽奖的
    private Long awardId;  // 用户抽取到的奖品id
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();
}
