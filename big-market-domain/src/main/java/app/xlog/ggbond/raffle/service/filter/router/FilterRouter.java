package app.xlog.ggbond.raffle.service.filter.router;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.service.armory.IStrategyDispatch;
import app.xlog.ggbond.raffle.service.armory.StrategyArmoryDispatch;
import app.xlog.ggbond.raffle.utils.SpringContextUtil;

public class FilterRouter implements IFilterRouter {

    private final IStrategyDispatch strategyDispatch;

    public FilterRouter() {
        this.strategyDispatch = SpringContextUtil.getBean(StrategyArmoryDispatch.class);
    }

    // 对Filter的结果进行处理，然后路由到指定的IStrategyDispatch
    @Override
    public FilterParam filterRouter(FilterParam filterParam) {
        // 增强switch
        Integer awardId = switch (filterParam.getDispatchParam()) {
            case CommonAwards -> strategyDispatch.getRuleCommonAwardIdByRandom(filterParam.getStrategyId());
            case LockAwards -> strategyDispatch.getRuleLockAwardIdByRandom(filterParam.getStrategyId());
            case LockLongAwards -> strategyDispatch.getRuleLockLongAwardIdByRandom(filterParam.getStrategyId());
            case BlacklistAward -> strategyDispatch.getWorstAwardId(filterParam.getStrategyId());
            case GrandAward -> strategyDispatch.getRuleGrandAwardIdByRandom(filterParam.getStrategyId());
        };

        filterParam.setAwardId(awardId);
        // 让其一定能进入到后置过滤器
        filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);
        return filterParam;
    }
}