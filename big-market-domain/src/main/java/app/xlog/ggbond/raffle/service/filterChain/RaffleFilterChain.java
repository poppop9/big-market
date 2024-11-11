package app.xlog.ggbond.raffle.service.filterChain;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RaffleFilterChain {

    @Resource
    private FlowExecutor flowExecutor;
    @Resource
    private RaffleFilterRouter raffleFilterRouter;

    /**
     * 执行过滤器链
     */
    public void executeFilterChain(RaffleFilterContext context) {
        log.atInfo().log("抽奖领域 - 前置过滤器链开始执行");
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("前置过滤器链", null, context);
        log.atInfo().log("抽奖领域 - 前置过滤器链执行完毕");

        log.atInfo().log("抽奖领域 - 路由调度开始执行");
        context = raffleFilterRouter.router(liteflowResponse.getContextBean(RaffleFilterContext.class));
        context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS);
        log.atInfo().log("抽奖领域 - 路由调度执行完毕");

        log.atInfo().log("抽奖领域 - 后置过滤器链开始执行");
        flowExecutor.execute2Resp("后置过滤器链", null, context);
        log.atInfo().log("抽奖领域 - 后置过滤器链执行完毕");
    }

}
