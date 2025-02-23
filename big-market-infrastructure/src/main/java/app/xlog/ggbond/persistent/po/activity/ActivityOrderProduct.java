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
public class ActivityOrderProduct extends SingleTable {
    private Long activityId;  // 活动id
    private @Builder.Default Long activityOrderProductId = IdUtil.getSnowflakeNextId();  // 活动单商品id
    private String activityOrderProductName;  // 活动单商品名称
    private Double activityOrderProductPrice;  // 活动单商品价格
    private Integer purchasingPower;  // 商品的购买力（单件商品可提供的抽奖次数）
}