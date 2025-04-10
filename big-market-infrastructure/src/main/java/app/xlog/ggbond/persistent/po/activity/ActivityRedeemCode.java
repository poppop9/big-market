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
@Comment("活动兑换码")
public class ActivityRedeemCode extends ShardingTable {
    private @Comment("活动ID") Long activityId;
    @Builder.Default
    private @Comment("兑换码") String redeemCode = IdUtil.randomUUID();
    private @Comment("该兑换码能兑换的抽奖次数") Long raffleCount;
    @Builder.Default
    private @Comment("是否已使用") Boolean isUsed = false;
    private @Comment("使用者ID") Long userId;
}