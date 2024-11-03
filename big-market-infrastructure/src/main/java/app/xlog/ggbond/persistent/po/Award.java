package app.xlog.ggbond.persistent.po;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import cn.zhxu.bs.bean.SearchBean;
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
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "strategyId")
    private Integer strategyId;  // 策略id
    @Column(name = "awardId")
    private Integer awardId;  // 奖品id
    @Column(name = "awardKey")
    private String awardKey;  //
    @Column(name = "awardConfig")
    private String awardConfig;  //
    @Column(name = "awardTitle")
    private String awardTitle;  // 奖品标题
    @Column(name = "awardSubtitle")
    private String awardSubtitle;  // 奖品副标题
    @Column(name = "awardCount")
    private Long awardCount;  // 奖品库存
    @Column(name = "awardRate")
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    @Column(name = "awardSort")
    private Integer awardSort;  // 奖品在前端的排序
    @Column(name = "rules")
    private String rules;  // 配置一些规则，数据类型是json，比如 { "rule_lock": "20" }，还有 { "rule_common_blacklist": "-1" }
    @Column(name = "createTime")
    private LocalDateTime createTime;
    @Column(name = "updateTime")
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
