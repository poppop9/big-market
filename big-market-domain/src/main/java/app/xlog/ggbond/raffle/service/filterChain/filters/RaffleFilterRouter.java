package app.xlog.ggbond.raffle.service.filterChain.filters;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.service.armory.IRaffleDispatch;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 抽奖过滤器路由
 */
@Slf4j
@LiteflowComponent
public class RaffleFilterRouter {

    @Resource
    private IRaffleDispatch raffleDispatch;

    /**
     * 判断是否继续执行过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.BOOLEAN,  // 组件类型 - 布尔组件
            value = LiteFlowMethodEnum.PROCESS_BOOLEAN,  // 组件方法类型 - boolean处理方法
            nodeId = "CheckProceed",
            nodeName = "是否继续执行过滤器")
    public boolean checkProceed(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        return context.getMiddleFilterParam() == RaffleFilterContext.MiddleFilterParam.PASS;
    }

    /**
     * 执行路由调度
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "RaffleFilterRouter",
            nodeName = "抽奖过滤器路由")
    public void raffleFilterRouter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);

        log.atInfo().log("抽奖领域 - 抽奖过滤器路由调度开始执行");
        Long awardId = raffleDispatch.findAwardIdByDispatchParam(context.getStrategyId(), context.getDispatchParam());

        context.setAwardId(awardId);
        context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS);
        log.atInfo().log("抽奖领域 - 抽奖过滤器路由调度执行完毕");
    }

}
