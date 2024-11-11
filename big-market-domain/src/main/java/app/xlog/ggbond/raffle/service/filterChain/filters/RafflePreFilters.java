package app.xlog.ggbond.raffle.service.filterChain.filters;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.service.filterChain.RaffleFilterRouter;
import app.xlog.ggbond.user.service.IUserService;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LiteflowComponent
public class RafflePreFilters {

    @Resource
    private IUserService userService;

    @Resource
    private RaffleFilterRouter raffleFilterRouter;

    // 黑名单过滤器
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "blacklistRaffleFilter",
            nodeName = "黑名单过滤器")
    public void blacklistRaffleFilter(NodeComponent bindCmp) {
        // todo 这个context如何传递给下一个组件
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);

        // 如果是黑名单用户，拦截
        if (userService.isBlacklistUser(context.getUserId())) {
            log.atInfo().log("抽奖领域 - 黑名单过滤器拦截");
            context.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            context.setDispatchParam(FilterParam.DispatchParam.BlacklistAward);

            // 立即结束整条过滤器链  todo 会结束这一次的吗？
            bindCmp.setIsEnd(true);
        } else {
            log.atInfo().log("抽奖领域 - 黑名单过滤器放行");
            context.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);
        }
    }


}
