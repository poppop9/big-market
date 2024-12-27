package app.xlog.ggbond.activity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单事件
 */
@Getter
@AllArgsConstructor
public enum OrderEvents {
    CreateOrder(201, "创建订单"),
    PAYING(102, "支付确认"),
    PAY_SUCCESS(200, "支付成功"),
    PAY_FAIL(402, "支付失败");

    private final int code;
    private final String info;
}
