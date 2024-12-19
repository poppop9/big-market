package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

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
