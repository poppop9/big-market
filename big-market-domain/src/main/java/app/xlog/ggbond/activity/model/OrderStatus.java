package app.xlog.ggbond.activity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
    INITIAL(202, "初始状态"),
    PAY_PENDING(102, "订单待支付"),
    PAY_SUCCESS(200, "订单已支付"),
    PAY_FAILED(402, "订单支付失败"),
    PAY_TIMEOUT(402, "订单支付超时"),
    CANCELED(410, "订单已取消");

    private final int code;
    private final String info;
}
