package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动单发放任务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderRewardTask", indexes = {
        // @Index(columnList = "activityOrderProductId"),
})
public class ActivityOrderRewardTask extends ShardingTableBaseEntity {
    private @Builder.Default Long activityOrderRewardTaskId = IdUtil.getSnowflakeNextId();  // 奖品发放任务id
    private @Builder.Default Boolean isIssued = false;  // 奖品是否发放

    // 为补偿扫描后的操作准备的字段
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long activityOrderId;  // 活动单id
    private Long activityOrderTypeId;  // 活动单类型id
    private Long raffleCount;  // 该类型的活动单能给予的抽奖次数
}