package app.xlog.ggbond.strategy.service.filter.router;

import app.xlog.ggbond.strategy.model.vo.FilterParam;
import app.xlog.ggbond.strategy.service.armory.IStrategyDispatch;
import jakarta.annotation.Resource;

public class FilterRouter implements IFilterRouter {

    @Resource
    private IStrategyDispatch strategyDispatch;

    // 对Filter的结果进行处理，然后路由到指定的IStrategyDispatch
    @Override
    public void filterRouter(FilterParam filterParam) {
        filterParam.getDispatchParam();

        switch (filterParam.getDispatchParam()) {
            case CommonAwards:
                strategyDispatch.getRuleCommonAwardIdByRandom(filterParam.getStrategyId());
                break;
            case LockAwards:
                strategyDispatch.getRuleLockAwardIdByRandom(filterParam.getStrategyId());
                break;
            case LockLongAwards:
                strategyDispatch.getRuleLockLongAwardIdByRandom(filterParam.getStrategyId());
                break;
            case BlacklistAward:
                strategyDispatch.getWorstAwardId(filterParam.getStrategyId());
                break;
        }
    }
}
