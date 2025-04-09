package app.xlog.ggbond.persistent.po.reward;

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
 * 积分流水表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PointsLog", indexes = {
        @Index(columnList = "pointsLogId"),
        @Index(columnList = "activityId"),
        @Index(columnList = "userId"),
        @Index(columnList = "activityId, userId"),
})
@Comment("积分流水表")
public class PointsLog extends ShardingTable {
    @Builder.Default
    private @Comment("积分流水ID") Long pointsLogId = IdUtil.getSnowflakeNextId();
    private @Comment("活动ID") Long activityId;
    private @Comment("用户ID") Long userId;
    private @Comment("积分数量") Long points;
    private @Comment("是否发放") Boolean isIssued;
}