package app.xlog.ggbond.strategy.service.filter.router;

import app.xlog.ggbond.strategy.model.vo.FilterParam;
import app.xlog.ggbond.strategy.service.armory.IStrategyDispatch;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class FilterRouter implements IFilterRouter {

    @Resource
    private IStrategyDispatch strategyDispatch;

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
        return filterParam;
    }
}
