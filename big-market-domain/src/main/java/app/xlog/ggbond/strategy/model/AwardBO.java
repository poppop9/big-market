package app.xlog.ggbond.strategy.model;

import app.xlog.ggbond.strategy.utils.SpringContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/*
对应infrastructure层的Award对象，用于业务逻辑处理，所以简化了一些属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardBO {
    private Integer strategyId;
    private Integer awardId;
    private String awardTitle;
    private String awardSubtitle;
    private Integer awardCount;
    private float awardRate;
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
