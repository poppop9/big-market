package app.xlog.ggbond.persistent.po.activity;

import app.xlog.ggbond.persistent.po.SingleTable;
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
 * 活动单商品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ActivityOrderProduct", indexes = {
        @Index(columnList = "activityId"),
        @Index(columnList = "activityOrderProductId"),
        @Index(columnList = "activityId, activityOrderProductId"),
})
@Comment("活动单商品")
public class ActivityOrderProduct extends SingleTable {
    private @Comment("活动ID") Long activityId;
    @Builder.Default
    private @Comment("活动单商品ID") Long activityOrderProductId = IdUtil.getSnowflakeNextId();
    private @Comment("活动单商品名称") String activityOrderProductName;
    private @Comment("活动单商品价格") Double activityOrderProductPrice;
    private @Comment("商品的购买力（单件商品可提供的抽奖次数）") Integer purchasingPower;
}