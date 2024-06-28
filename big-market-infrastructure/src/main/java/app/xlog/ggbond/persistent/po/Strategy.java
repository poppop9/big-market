package app.xlog.ggbond.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("strategy")
public class Strategy {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("strategy_id")
    private int strategyId;
    @TableField("strategy_desc")
    private String strategyDesc;
    @TableField("rules")
    private String rules;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
}
