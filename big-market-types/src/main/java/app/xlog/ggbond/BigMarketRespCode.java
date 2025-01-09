package app.xlog.ggbond;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务级别的响应码
 */
@Getter
@AllArgsConstructor
public enum BigMarketRespCode {
    // 通用错误码
    SUCCESS(200, "请求成功"),
    ERROR(500, "请求失败"),
    PARAMETER_VERIFICATION_FAILED(600, "参数校验失败"),
    // 活动领域
    ACTIVITY_ORDER_INSUFFICIENT_INVENTORY(520, "活动单库存不足"),
    ;

    private final int code;
    private final String message;
}
