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
    SUCCESS(100, "请求成功"),
    ERROR(101, "请求失败"),
    PARAMETER_VERIFICATION_FAILED(103, "参数校验失败"),
    // 活动领域
    ACTIVITY_ORDER_INSUFFICIENT_INVENTORY(300, "活动单库存不足"),
    ACTIVITY_SIGN_IN_TO_CLAIM_AO_EXIST(301, "今天已签到领取过"),
    ACTIVITY_REDEEM_CODE_ERROR(302, "活动兑换码错误"),
    // 抽奖领域
    DECREASE_AWARD_COUNT_FAILED(400, "扣减奖品库存失败，可能由于并发太高，或者奖品库存没有装配"),
    USER_IS_IN_RAFFLE(401, "该用户已经在抽奖中"),
    // 安全领域
    WRONG_USERNAME_OR_PASSWORD(700, "用户名或密码错误"),
    // 推荐领域
    AI_RESPONSE_ERROR(800, "AI 的回答格式错误"),
    ;

    private final int code;
    private final String message;
}
