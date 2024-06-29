package app.xlog.ggbond.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
}
