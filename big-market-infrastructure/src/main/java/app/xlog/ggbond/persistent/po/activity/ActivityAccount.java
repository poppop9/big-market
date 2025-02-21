package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.ShardingTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动账户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityAccount", indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "activityId")
})
public class ActivityAccount extends ShardingTable {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long availableRaffleCount;  // 可用的抽奖次数
    private @Builder.Default Double balance = 0d;  // 余额
    private @Builder.Default Long points = 0L;  // 积分
}