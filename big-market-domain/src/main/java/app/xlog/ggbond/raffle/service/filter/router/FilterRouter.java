package app.xlog.ggbond.raffle.service.filter.router;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.service.armory.IRaffleDispatch;
import app.xlog.ggbond.raffle.utils.SpringContextUtil;

public class FilterRouter implements IFilterRouter {

    private final IRaffleDispatch raffleDispatch;

    public FilterRouter() {
        this.raffleDispatch = SpringContextUtil.getBean(IRaffleDispatch.class);
    }

    // 对Filter的结果进行处理，然后路由到指定的IStrategyDispatch
    @Override
    public FilterParam filterRouter(FilterParam filterParam) {
        // 增强switch
        Long awardId = switch (filterParam.getDispatchParam()) {
            case CommonAwards -> raffleDispatch.getRuleCommonAwardIdByRandom(filterParam.getStrategyId());
            case LockAwards -> raffleDispatch.getRuleLockAwardIdByRandom(filterParam.getStrategyId());
            case LockLongAwards -> raffleDispatch.getRuleLockLongAwardIdByRandom(filterParam.getStrategyId());
            case BlacklistAward -> raffleDispatch.getWorstAwardId(filterParam.getStrategyId());
            case GrandAward -> raffleDispatch.getRuleGrandAwardIdByRandom(filterParam.getStrategyId());
        };

        filterParam.setAwardId(awardId);
        // 让其一定能进入到后置过滤器
        filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);
        return filterParam;
    }
}