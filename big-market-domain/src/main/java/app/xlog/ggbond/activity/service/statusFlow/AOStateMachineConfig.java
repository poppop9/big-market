package app.xlog.ggbond.activity.service.statusFlow;

import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.bo.ActivityOrderRewardTaskBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 状态机配置
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
    public StateMachine<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext> stateMachine() {
        StateMachineBuilder<ActivityOrderBO.ActivityOrderStatus, ActivityOrderBO.ActivityOrderEvent, AOContext> builder = StateMachineBuilderFactory.create();

        /*
          外部 - 创建待支付的活动单 : 初始状态 -> 待支付状态
         */
        builder.externalTransition()
                .from(ActivityOrderBO.ActivityOrderStatus.INITIAL)
                .to(ActivityOrderBO.ActivityOrderStatus.PENDING_PAYMENT)
                .on(ActivityOrderBO.ActivityOrderEvent.INITIAL_TO_PENDING_PAYMENT)
                .when(context -> true)
                .perform((S1, S2, E, C) -> {
                    LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("INITIAL_TO_PENDING_PAYMENT", null, C);
                    if (!liteflowResponse.isSuccess()) throw (RuntimeException) liteflowResponse.getCause();
                });

        /*
          外部 - 关闭待支付的活动单 : 待支付状态 -> 已关闭状态
         */
        builder.externalTransition()
                .from(ActivityOrderBO.ActivityOrderStatus.PENDING_PAYMENT)
                .to(ActivityOrderBO.ActivityOrderStatus.CLOSED)
                .on(ActivityOrderBO.ActivityOrderEvent.PENDING_PAYMENT_TO_CLOSED)
                .when(context -> true)
                .perform((S1, S2, E, C) -> {
                    LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("PENDING_PAYMENT_TO_CLOSED", null, C);
                    if (!liteflowResponse.isSuccess()) throw (RuntimeException) liteflowResponse.getCause();
                });

        /*
          外部 - 待支付活动单转有效活动单 : 待支付状态 -> 有效状态
         */
        builder.externalTransition()
                .from(ActivityOrderBO.ActivityOrderStatus.PENDING_PAYMENT)
                .to(ActivityOrderBO.ActivityOrderStatus.EFFECTIVE)
                .on(ActivityOrderBO.ActivityOrderEvent.PENDING_PAYMENT_TO_EFFECTIVE)
                .when(context -> {
                    // 执行待支付状态 -> 有效状态条件判断链
                    LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("WHEN_PENDING_PAYMENT_TO_EFFECTIVE", null, context);
                    if (!liteflowResponse.isSuccess()) throw (RuntimeException) liteflowResponse.getCause();

                    context = liteflowResponse.getContextBean(AOContext.class);
                    Boolean isConditionMet = context.getIsConditionMet();

                    if (!isConditionMet)
                        log.atInfo().log("活动领域 - 用户 {} WHEN_PENDING_PAYMENT_TO_EFFECTIVE 规则链不满足条件", context.getUserId());
                    return isConditionMet;
                })
                .perform((S1, S2, E, C) -> {
                    Long activityOrderRewardIdTaskId = activityRepo.insertActivityOrderRewardTask(ActivityOrderRewardTaskBO.builder()
                            .userId(C.getUserId())
                            .activityId(C.getActivityId())
                            .activityOrderId(C.getActivityOrderBO().getActivityOrderId())
                            .activityOrderTypeId(C.getActivityOrderType().getActivityOrderTypeId())
                            .raffleCount(C.getRaffleCount())
                            .build());
                    C.setActivityOrderRewardTaskId(activityOrderRewardIdTaskId);
                    activityRepo.sendRewardEffectiveActivityOrderTaskToMQ(C);
                });

        /*
          外部 - 有效活动单转已使用活动单 : 有效状态 -> 已使用状态
         */
        builder.externalTransition()
                .from(ActivityOrderBO.ActivityOrderStatus.EFFECTIVE)
                .to(ActivityOrderBO.ActivityOrderStatus.USED)
                .on(ActivityOrderBO.ActivityOrderEvent.EFFECTIVE_TO_USED)
                .when(context -> {
                    // 执行有效状态 -> 已使用状态条件判断链
                    LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("WHEN_EFFECTIVE_TO_USED", null, context);
                    if (!liteflowResponse.isSuccess()) throw (RuntimeException) liteflowResponse.getCause();

                    context = liteflowResponse.getContextBean(AOContext.class);
                    Boolean isConditionMet = context.getIsConditionMet();

                    if (!isConditionMet)
                        log.atInfo().log("活动领域 - 用户 {} WHEN_EFFECTIVE_TO_USED 规则链不满足条件", context.getUserId());
                    return isConditionMet;
                })
                .perform((S1, S2, E, C) -> {
                    // 执行有效状态 -> 已使用状态活动单生成链
                    LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("EFFECTIVE_TO_USED", null, C);
                    if (!liteflowResponse.isSuccess()) throw (RuntimeException) liteflowResponse.getCause();
                });

        // 创建状态机
        return builder.build(AOStateMachineConfig.ACTIVITY_ORDER_MACHINE_ID);
    }

}

