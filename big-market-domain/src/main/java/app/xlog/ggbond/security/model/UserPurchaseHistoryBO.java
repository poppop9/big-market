package app.xlog.ggbond.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户购买历史
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPurchaseHistoryBO {
    private Long userId;  // 用户id
    private String purchaseName;  // 商品名称
    private String purchaseCategory;  // 商品类型（生鲜、家居、数码 ……）
    private double purchasePrice;  // 商品价格
    private boolean purchaseCount;  // 购买数量
    private boolean purchaseTimes;  // 此次是第几次购买
    private boolean isReturn;  // 是否退货
}
