package app.xlog.ggbond.resp;

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
    ACTIVITY_REDEEM_CODE_ERROR(302, "活动兑换码错误或已被使用"),
    NOT_EFFECTIVE_ACTIVITY_ORDER(303, "没有有效的活动单"),
    ACTIVITY_ORDER_IS_USED(304, "该活动单的可用抽奖次数已满，重新调度"),
    USER_IS_IN_CONSUME_AO(305, "该用户正在消费活动单"),
    ACTIVITY_BALANCE_NOT_ENOUGH(306, "用户余额不足"),
    ACTIVITY_ORDER_PRODUCT_NOT_EXIST(307, "活动单商品不存在"),
    // 抽奖领域
    DECREASE_AWARD_COUNT_FAILED(400, "扣减奖品库存失败，可能由于并发太高，或者奖品库存没有装配"),
    USER_IS_IN_RAFFLE(401, "该用户已经在抽奖中"),
    RAFFLE_CONFIG_ARMORY_ERROR(402, "抽奖相关配置装配失败"),
    // 安全领域
    WRONG_USERNAME_OR_PASSWORD(700, "用户名或密码错误"),
    FREQUENT_LOGIN(701, "登录频繁"),
    // 推荐领域
    AI_RESPONSE_ERROR(800, "AI 的回答格式错误"),
    ;

    private final int code;
    private final String message;
}
