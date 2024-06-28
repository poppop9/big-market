package app.xlog.ggbond.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("award_rule")
public class AwardRule {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("award_id")
    private Integer awardId;
    @TableField("rule_name")
    private String ruleName;
    @TableField("rule_value")
    private Integer ruleValue;
    @TableField("rule_desc")
    private String ruleDesc;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
}
