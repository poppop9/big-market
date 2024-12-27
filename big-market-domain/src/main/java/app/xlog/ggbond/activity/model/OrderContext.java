package app.xlog.ggbond.activity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 订单上下文
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderContext {
    private Long orderId;  // 订单id
    private Long payAmount;  // 支付金额
}
