package app.xlog.ggbond.persistent.po.activity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动账户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// @Entity
@Table(name = "ActivityAccount", indexes = {
        // @Index(columnList = "userId")
})
public class ActivityAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    private Long userId;  // 用户id
    private Long activityId;  // 活动id
}
