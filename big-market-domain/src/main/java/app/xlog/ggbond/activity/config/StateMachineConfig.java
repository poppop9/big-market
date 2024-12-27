package app.xlog.ggbond.activity.config;

import app.xlog.ggbond.activity.model.OrderContext;
import app.xlog.ggbond.activity.model.OrderEvents;
import app.xlog.ggbond.activity.model.OrderStatus;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 状态机配置
 * <p>
 * - 配置不同状态下对不同事件的响应
 */
@Slf4j
@Configuration
public class StateMachineConfig {

    public static final String ORDER_MACHINE_ID = "order_state_machine";  // 订单状态机ID

    @Bean
    public StateMachine<OrderStatus, OrderEvents, OrderContext> stateMachine() {
        StateMachineBuilder<OrderStatus, OrderEvents, OrderContext> builder = StateMachineBuilderFactory.create();

        // 外部 - 创建订单：初始状态 -> 待支付状态
        builder.externalTransition().from(OrderStatus.INITIAL).to(OrderStatus.PAY_PENDING)
                .on(OrderEvents.CreateOrder)  // 触发事件
                .when(context -> context.getPayAmount() > 0)  // 流转条件
                .perform((S1, S2, E, C) -> log.info("订单创建成功，订单id：{}", C.getOrderId()));  // 动作

        // 外部 - 支付失败：订单已取消、支付超时状态 -> 支付失败状态
        builder.externalTransitions().fromAmong(OrderStatus.CANCELED, OrderStatus.PAY_TIMEOUT).to(OrderStatus.PAY_FAILED)
                .on(OrderEvents.PAY_FAIL)
                .when(context -> context.getPayAmount() > 0)
                .perform((S1, S2, E, C) -> log.info("订单支付失败，订单id：{}", C.getOrderId()));

        // 内部 - 支付失败：支付失败状态 -> 支付失败状态
        builder.internalTransition().within(OrderStatus.PAY_FAILED)
                .on(OrderEvents.PAY_FAIL)
                .when(context -> true)
                .perform((S1, S2, E, C) -> log.info("支付失败，订单id：{}", C.getOrderId()));

        // 创建状态机
        return builder.build(StateMachineConfig.ORDER_MACHINE_ID);
    }

}

