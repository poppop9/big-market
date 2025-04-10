package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.ShardingTable;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 活动单发放任务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderRewardTask", indexes = {
        @Index(columnList = "activityOrderRewardTaskId"),
        @Index(columnList = "isIssued"),
        @Index(columnList = "activityOrderRewardTaskId, isIssued"),
})
@Comment("活动单发放任务")
public class ActivityOrderRewardTask extends ShardingTable {
    @Builder.Default
    private @Comment("奖品发放任务ID") Long activityOrderRewardTaskId = IdUtil.getSnowflakeNextId();
    @Builder.Default
    private @Comment("奖品是否发放") Boolean isIssued = false;

    // 为补偿扫描后的操作准备的字段
    private @Comment("用户ID") Long userId;
    private @Comment("活动ID") Long activityId;
    private @Comment("活动单ID") Long activityOrderId;
    private @Comment("活动单类型ID") Long activityOrderTypeId;
    private @Comment("该类型的活动单能给予的抽奖次数") Long raffleCount;
}