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

/**
 * 活动兑换码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityRedeemCode", indexes = {
        @Index(columnList = "activityId"),
        @Index(columnList = "redeemCode"),
        @Index(columnList = "activityId, redeemCode"),
        @Index(columnList = "userId"),
})
public class ActivityRedeemCode extends ShardingTable {
    private Long activityId;  // 活动id
    private @Builder.Default String redeemCode = IdUtil.randomUUID();  // 兑换码
    private Long raffleCount;  // 该兑换码能兑换的抽奖次数
    private @Builder.Default Boolean isUsed = false;  // 是否已使用
    private Long userId;  // 使用者id
}