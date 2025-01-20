package app.xlog.ggbond.activity.service;

import app.xlog.ggbond.activity.config.StateMachineConfig;
import app.xlog.ggbond.activity.model.ActivityOrderContext;
import app.xlog.ggbond.activity.model.ActivityOrderBO;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.StateMachineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 状态机事件中心
 */
@Slf4j
@Component
public class ActivityOrderEventCenter {

    /**
     * 发布事件 - 创建活动单
     */
    public boolean publishCreateActivityOrderEvent(ActivityOrderContext activityOrderContext) {
        ActivityOrderBO.ActivityOrderStatus activityOrderStatus = StateMachineFactory.<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvents, ActivityOrderContext>get(StateMachineConfig.ACTIVITY_ORDER_MACHINE_ID)
                .fireEvent(
                        ActivityOrderBO.ActivityOrderStatus.INITIAL,
                        ActivityOrderBO.ActivityOrderEvents.CreateActivityOrder,
                        activityOrderContext
                );
        if (activityOrderStatus == ActivityOrderBO.ActivityOrderStatus.NOT_USED) {
            log.debug("活动单创建成功 {}", activityOrderContext);
            return true;
        } else {
            log.error("活动单创建失败 {}", activityOrderContext);
            return false;
        }
    }

    /**
     * 测试 - 测试方法
     */
    public void test() {
        StateMachine<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvents, ActivityOrderContext> stateMachine = StateMachineFactory.<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvents, ActivityOrderContext>get(StateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
        String s = stateMachine.generatePlantUML();
        System.out.println(s);
    }

}
