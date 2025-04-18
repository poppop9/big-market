package app.xlog.ggbond.activity.service.statusFlow;

import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.exception.BigMarketException;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.StateMachineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 状态机事件中心
 */
@Slf4j
@Component
public class AOEventCenter {

    /**
     * 发布事件 - 创建待支付活动单
     */
    @Transactional
    public AOContext publishInitialToPendingPaymentEvent(AOContext aoContext) {
        StateMachine<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext> stateMachine = StateMachineFactory.get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
        stateMachine.fireEvent(
                ActivityOrderBO.ActivityOrderStatus.INITIAL,
                ActivityOrderBO.ActivityOrderEvent.INITIAL_TO_PENDING_PAYMENT,
                aoContext
        );

        return aoContext;
    }

    /**
     * 发布事件 - 待支付活动单转有效活动单
     */
    @Transactional
    public AOContext publishPendingPaymentToEffectiveEvent(AOContext aoContext) {
        StateMachineFactory.<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext>get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID)
                .fireEvent(
                        ActivityOrderBO.ActivityOrderStatus.PENDING_PAYMENT,
                        ActivityOrderBO.ActivityOrderEvent.PENDING_PAYMENT_TO_EFFECTIVE,
                        aoContext
                );
        return aoContext;
    }

    /**
     * 发布事件 - 有效活动单转已使用活动单
     */
    public AOContext publishEffectiveToUsedEvent(AOContext aoContext) {
        StateMachineFactory.<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext>get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID)
                .fireEvent(
                        ActivityOrderBO.ActivityOrderStatus.EFFECTIVE,
                        ActivityOrderBO.ActivityOrderEvent.EFFECTIVE_TO_USED,
                        aoContext
                );
        return aoContext;
    }

    /**
     * 发布事件 - 待支付活动单转已关闭活动单
     */
    public AOContext publishPendingPaymentToClosedEvent(AOContext aoContext) {
        StateMachineFactory.<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext>get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID)
                .fireEvent(
                        ActivityOrderBO.ActivityOrderStatus.PENDING_PAYMENT,
                        ActivityOrderBO.ActivityOrderEvent.PENDING_PAYMENT_TO_CLOSED,
                        aoContext
                );
        return aoContext;
    }

    /**
     * 发布事件 - 有效活动单转已过期活动单
     */
    public void publishEffectiveToExpiredEvent(Long activityOrderId) {
        StateMachineFactory.<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext>get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID)
                .fireEvent(
                        ActivityOrderBO.ActivityOrderStatus.EFFECTIVE,
                        ActivityOrderBO.ActivityOrderEvent.EFFECTIVE_TO_EXPIRED,
                        AOContext.builder()
                                .activityOrderBO(ActivityOrderBO.builder()
                                        .activityOrderId(activityOrderId)
                                        .build())
                                .build()
                );
    }

    /**
     * 测试 - 测试方法
     */
    public void test() {
        StateMachine<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext> stateMachine = StateMachineFactory.get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
        String s = stateMachine.generatePlantUML();
        System.out.println(s);
    }

}
