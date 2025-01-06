package app.xlog.ggbond.activity.config;

import app.xlog.ggbond.activity.model.ActivityOrderContext;
import app.xlog.ggbond.activity.model.ActivityOrderFlowBO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import jakarta.annotation.Resource;
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

    public static final String ACTIVITY_ORDER_MACHINE_ID = "activity_order_state_machine";  // 订单状态机ID

    @Resource
    private IActivityRepo activityRepo;

    @Bean
    public StateMachine<ActivityOrderFlowBO.ActivityOrderStatus, ActivityOrderFlowBO.ActivityOrderEvents, ActivityOrderContext> stateMachine() {
        StateMachineBuilder<ActivityOrderFlowBO.ActivityOrderStatus, ActivityOrderFlowBO.ActivityOrderEvents, ActivityOrderContext> builder = StateMachineBuilderFactory.create();

        /*
          外部 - 创建订单：初始状态 -> 未使用状态
         */
        builder.externalTransition().from(ActivityOrderFlowBO.ActivityOrderStatus.INITIAL).to(ActivityOrderFlowBO.ActivityOrderStatus.NOT_USED)
                .on(ActivityOrderFlowBO.ActivityOrderEvents.CreateActivityOrder)
                .when(context -> true)
                .perform((S1, S2, E, C) -> {
                    // 将活动单创建，并插入数据库
                    activityRepo.saveActivityOrderFlow(BeanUtil
                            .copyProperties(C, ActivityOrderFlowBO.class)
                            .setActivityOrderStatus(ActivityOrderFlowBO.ActivityOrderStatus.NOT_USED)
                    );

                    // 如果该活动单有效，则更新redis缓存
                });

        // 创建状态机
        return builder.build(StateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
    }

}

