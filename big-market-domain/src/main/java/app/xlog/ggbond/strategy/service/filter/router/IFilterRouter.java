package app.xlog.ggbond.strategy.service.filter.router;

import app.xlog.ggbond.strategy.model.vo.FilterParam;

// 这是Filter和IStrategyDispatch的中间层，将Filter的结果进行处理，然后指定调度哪一个dispatch
public interface IFilterRouter {
    void filterRouter(FilterParam filterParam);
}
