package app.xlog.ggbond.activity;

import app.xlog.ggbond.activity.config.StateMachineConfig;
import app.xlog.ggbond.activity.model.OrderContext;
import app.xlog.ggbond.activity.model.OrderEvents;
import app.xlog.ggbond.activity.model.OrderStatus;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.StateMachineFactory;
import org.springframework.stereotype.Component;

/**
 * 状态机事件中心
 */
@Component
public class OrderStateMachineEventCenter {

    /**
     * 发布创建订单事件
     */
    public void publishCreateOrderEvent() {
        OrderStatus orderStatus = StateMachineFactory.<OrderStatus, OrderEvents, OrderContext>get(StateMachineConfig.ORDER_MACHINE_ID)
                .fireEvent(
                        OrderStatus.INITIAL,
                        OrderEvents.CreateOrder,
                        new OrderContext().setOrderId(12345L).setPayAmount(100L)
                );
        if (orderStatus == OrderStatus.PAY_PENDING) {
            // 订单创建成功
        } else {
            // 订单创建失败
        }
    }

    /**
     * 测试方法
     */
    public void test() {
        StateMachine<OrderStatus, OrderEvents, OrderContext> stateMachine = StateMachineFactory.<OrderStatus, OrderEvents, OrderContext>get(StateMachineConfig.ORDER_MACHINE_ID);
        String s = stateMachine.generatePlantUML();
        System.out.println(s);
    }

}
