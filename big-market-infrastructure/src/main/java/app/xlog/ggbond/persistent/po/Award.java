package app.xlog.ggbond.persistent.po;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 奖品表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Award")
@TableName("Award")
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer strategyId;  // 策略id
    private Integer awardId;  // 奖品id
    private String awardKey;  //
    private String awardConfig;  //
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
    private Long awardCount;  // 奖品库存
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    private Integer awardSort;  // 奖品在前端的排序
    private String rules;  // 配置一些规则，数据类型是json，比如 { "rule_lock": "20" }，还有 { "rule_common_blacklist": "-1" }
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

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
