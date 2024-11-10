package app.xlog.ggbond.raffle.model;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
对应infrastructure层的Award对象，用于业务逻辑处理，所以简化了一些属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardBO {
    private Long strategyId;
    private Long awardId;
    private String awardTitle;
    private String awardSubtitle;
    private Long awardCount;
    private Double awardRate;
    private Integer awardSort;
    private String rules;

    /*
    为了将rules字段转为ObjectNode对象
     */
    public ObjectNode stringToObjectNode(String s) throws JsonProcessingException {
        // 手动获取Bean
        ObjectMapper objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
        return objectMapper.readValue(s.replace("\\", ""), ObjectNode.class);
    }
}
