package app.xlog.ggbond.raffle.service.filterChain;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.service.filterChain.filters.RaffleFilterRouter;
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

    /**
     * 执行过滤器链
     */
    public Long executeFilterChain(RaffleFilterContext context) {
        log.atInfo().log("抽奖领域 - " + context.getUserId() + " 过滤器链开始执行");
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("RaffleFilterChain", null, context);
        log.atInfo().log("抽奖领域 - " + context.getUserId() + " 过滤器链执行完毕");

        return liteflowResponse.getContextBean(RaffleFilterContext.class).getAwardId();
    }

}