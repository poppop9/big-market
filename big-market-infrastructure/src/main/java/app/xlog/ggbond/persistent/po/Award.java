package app.xlog.ggbond.persistent.po;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("award")
public class Award {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("strategy_id")
    private Integer strategyId;
    @TableField("award_id")
    private Integer awardId;
    @TableField("award_key")
    private String awardKey;
    @TableField("award_config")
    private String awardConfig;
    @TableField("award_title")
    private String awardTitle;
    @TableField("award_subtitle")
    private String awardSubtitle;
    @TableField("award_count")
    private Integer awardCount;
    @TableField("award_rate")
    private float awardRate;
    // 奖品在前端的排序
    @TableField("award_sort")
    private Integer awardSort;
    @TableField("rules")
    private String rules;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    /*
    为了将rules字段转为ObjectNode对象
     */
    public ObjectNode stringToObjectNode(String s) throws JsonProcessingException {
        // 手动获取Bean
        ObjectMapper objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
        s = s.replace("\\", "");
        return objectMapper.readValue(s, ObjectNode.class);
    }
}
