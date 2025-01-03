package app.xlog.ggbond.activity.model;

import cn.hutool.core.util.IdUtil;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 活动订单流水
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ActivityOrderFlowBO {
    private Long userId;  // 用户id
    private Long activityId;  // 活动id
    private Long strategyId;  // 策略id
    private Long activityOrderId;  // 活动单id
    private LocalDateTime activityOrderEffectiveTime;  // 订单生效时间（立马生效为LocalDateTime.MIN）
    private LocalDateTime activityOrderExpireTime;  // 订单过期时间（永久有效为LocalDateTime.MAX）
    private ActivityOrderType activityOrderType;  // 订单类型
    private ActivityOrderStatus activityOrderStatus;  // 订单状态

    public enum ActivityOrderType {
        SIGN_IN_TO_CLAIM,  // 签到领取
        FREE_GIVEAWAY,  // 免费赠送
        PAID_PURCHASE,  // 付费购买
        REDEEM_TO_OBTAIN  // 兑换获取
    }

    /**
     * 活动单状态
     */
    @Getter
    @AllArgsConstructor
    public enum ActivityOrderStatus {
        INITIAL(100,"初始状态"),
        NOT_USED(204,"未使用"),
        USED(200,"已使用"),
        EXPIRED(410,"已失效");

        private final int code;
        private final String info;
    }

    /**
     * 活动单事件
     */
    @Getter
    @AllArgsConstructor
    public enum ActivityOrderEvents {
        CreateActivityOrder(201, "创建活动订单"),
        PAYING(102, "支付确认"),
        PAY_SUCCESS(200, "支付成功"),
        PAY_FAIL(402, "支付失败");

        private final int code;
        private final String info;
    }
}
