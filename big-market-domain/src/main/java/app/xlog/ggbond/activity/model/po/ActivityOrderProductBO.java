package app.xlog.ggbond.activity.model.po;

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
public class ActivityOrderProductBO {
    private Long activityOrderProductId;  // 活动单商品id
    private String activityOrderProductName;  // 活动单商品名称
    private Double activityOrderProductPrice;  // 活动单商品价格
    private Integer purchasingPower;  // 商品的购买力（单件商品可提供的抽奖次数）
}