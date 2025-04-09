package app.xlog.ggbond.persistent.po;

import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 单表基类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class SingleTable {
    @Id
    private Long id = IdUtil.getSnowflakeNextId();
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;
}