package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
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
        @Index(columnList = "awardId")
})
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long strategyId;  // 策略id
    private Long awardId;  // 奖品id
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
    private Long awardCount;  // 奖品库存
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    private Integer awardSort;  // 奖品在前端的排序

    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 将rules字段转为ObjectNode对象
     */
    public ObjectNode stringToObjectNode(String s) throws JsonProcessingException {
        // 手动获取Bean
        ObjectMapper objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
        s = s.replace("\\", "");
        return objectMapper.readValue(s, ObjectNode.class);
    }
}
