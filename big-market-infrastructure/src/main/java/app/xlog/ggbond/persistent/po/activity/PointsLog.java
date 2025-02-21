package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.ShardingTable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 积分流水表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PointsLog", indexes = {
        // @Index(columnList = "activityId"),
})
public class PointsLog extends ShardingTable {
    private Long pointsLogId;  // 积分流水id
    private Long activityId;  // 活动id
    private Long userId;  // 用户id
    private Long points;  // 积分
}
