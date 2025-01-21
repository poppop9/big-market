package app.xlog.ggbond.activity.service.statusFlow;

import app.xlog.ggbond.BigMarketException;
import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.raffle.model.vo.RetryRouterException;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
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
public class AOStateMachineConfig {

    public static final String ACTIVITY_ORDER_MACHINE_ID = "activity_order_state_machine";  // 订单状态机ID

    @Resource
    private FlowExecutor flowExecutor;
    @Resource
    private IActivityRepo activityRepo;

    @Bean
    public StateMachine<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvents, AOContext> stateMachine() {
        StateMachineBuilder<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvents, AOContext> builder = StateMachineBuilderFactory.create();

        /*
          外部 - 创建订单：初始状态 -> 有效状态
         */
        builder.externalTransition()
                .from(ActivityOrderBO.ActivityOrderStatus.INITIAL)
                .to(ActivityOrderBO.ActivityOrderStatus.EFFECTIVE)
                .on(ActivityOrderBO.ActivityOrderEvents.CreateActivityOrder)
                .when(context -> {
                    // 执行活动单条件判断链
                    LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("CreateActivityOrderWhenChain", null, context);
                    context = liteflowResponse.getContextBean(AOContext.class);
                    return context.getIsConditionMet();
                })
                .perform((S1, S2, E, C) -> {
                    // 执行活动单生成链
                    LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("CreateActivityOrderPerformChain", null, C);
                    if (!liteflowResponse.isSuccess()) throw (RuntimeException) liteflowResponse.getCause();
                });

        // 创建状态机
        return builder.build(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
    }

}

