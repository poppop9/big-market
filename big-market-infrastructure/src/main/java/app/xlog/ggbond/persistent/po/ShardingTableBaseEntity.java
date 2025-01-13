package app.xlog.ggbond.persistent.po;

import app.xlog.ggbond.persistent.util.JpaDefaultValueListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * 分片表基类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(JpaDefaultValueListener.class) // 添加注解处理器
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