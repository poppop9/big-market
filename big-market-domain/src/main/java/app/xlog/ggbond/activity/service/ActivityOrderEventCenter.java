package app.xlog.ggbond.activity.service;

import app.xlog.ggbond.activity.config.StateMachineConfig;
import app.xlog.ggbond.activity.model.ActivityOrderContext;
import app.xlog.ggbond.activity.model.ActivityOrderFlowBO;
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
        ActivityOrderFlowBO.ActivityOrderStatus activityOrderStatus = StateMachineFactory.<ActivityOrderFlowBO.ActivityOrderStatus, ActivityOrderFlowBO.ActivityOrderEvents, ActivityOrderContext>get(StateMachineConfig.ACTIVITY_ORDER_MACHINE_ID)
                .fireEvent(
                        ActivityOrderFlowBO.ActivityOrderStatus.INITIAL,
                        ActivityOrderFlowBO.ActivityOrderEvents.CreateActivityOrder,
                        activityOrderContext
                );
        if (activityOrderStatus == ActivityOrderFlowBO.ActivityOrderStatus.NOT_USED) {
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
        StateMachine<ActivityOrderFlowBO.ActivityOrderStatus, ActivityOrderFlowBO.ActivityOrderEvents, ActivityOrderContext> stateMachine = StateMachineFactory.<ActivityOrderFlowBO.ActivityOrderStatus, ActivityOrderFlowBO.ActivityOrderEvents, ActivityOrderContext>get(StateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
        String s = stateMachine.generatePlantUML();
        System.out.println(s);
    }

}
