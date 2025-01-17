package app.xlog.ggbond.persistent.po;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 分片表基类
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@MappedSuperclass
public class ShardingTableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createTime;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime updateTime;
}