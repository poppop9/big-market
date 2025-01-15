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
    ACTIVITY_ORDER_INSUFFICIENT_INVENTORY(510, "活动单库存不足"),
    // 抽奖领域
    DECREASE_AWARD_COUNT_FAILED(550, "扣减奖品库存失败，可能由于并发太高，或者奖品库存没有装配"),
    // 安全领域
    WRONG_USERNAME_OR_PASSWORD(580, "用户名或密码错误"),
    // 推荐领域
    AI_RESPONSE_ERROR(590, "AI 的回答格式错误"),
    ;

    private final int code;
    private final String message;
}
