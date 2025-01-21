package app.xlog.ggbond.activity.service.statusFlow;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.StateMachineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 状态机事件中心
 */
@Slf4j
@Component
public class AOEventCenter {

    /**
     * 发布事件 - 创建活动单
     */
    public void publishCreateActivityOrderEvent(AOContext AOContext) {
        StateMachineFactory.<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvents, AOContext>get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID)
                .fireEvent(
                        ActivityOrderBO.ActivityOrderStatus.INITIAL,
                        ActivityOrderBO.ActivityOrderEvents.CreateActivityOrder,
                        AOContext
                );
    }

    /**
     * 测试 - 测试方法
     */
    public void test() {
        StateMachine<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvents, AOContext> stateMachine = StateMachineFactory.get(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
        String s = stateMachine.generatePlantUML();
        System.out.println(s);
    }

}
