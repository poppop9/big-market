package app.xlog.ggbond.persistent.po.raffle;

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
 * 奖品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Award",indexes = {
        @Index(columnList = "strategyId"),
        @Index(columnList = "awardId"),
        @Index(columnList = "awardCount"),
        @Index(columnList = "strategyId, awardId")
})
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime createTime;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime updateTime;

    private Long strategyId;  // 策略id
    @Column(unique = true)
    @Builder.Default
    private Long awardId = IdUtil.getSnowflakeNextId();  // 奖品id
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
    private Long awardCount;  // 奖品库存
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    private Integer awardSort;  // 奖品在前端的排序
}
