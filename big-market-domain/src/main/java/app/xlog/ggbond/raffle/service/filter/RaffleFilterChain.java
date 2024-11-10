package app.xlog.ggbond.raffle.service.filter;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.service.filter.router.FilterRouter;
import app.xlog.ggbond.raffle.service.filter.router.IFilterRouter;

import java.util.ArrayList;
import java.util.List;

// 抽奖过滤器链，用于定义抽奖过滤器的数量和执行顺序
public class RaffleFilterChain {

    private final IFilterRouter filterRouter = new FilterRouter();

    // 前置过滤器链
    private final List<RaffleFilter> preFilters = new ArrayList<>();
    // 后置过滤器链
    private final List<RaffleFilter> afterFilters = new ArrayList<>();

    /**
     * 添加过滤器
     **/
    public RaffleFilterChain addPreFilter(RaffleFilter raffleFilter) {
        preFilters.add(raffleFilter);
        return this;
    }
    public RaffleFilterChain addAfterFilter(RaffleFilter raffleFilter) {
        afterFilters.add(raffleFilter);
        return this;
    }

    /**
     * 执行整条过滤器链
     **/
    public FilterParam doFilter(FilterParam filterParam) {
        // +++++++++++++++++++++++++
        // +++++ 执行前置过滤器链 +++++
        // +++++++++++++++++++++++++
        for (RaffleFilter raffleFilter : preFilters) {
            // 如果上一个过滤器拦截了，那么就不再继续执行
            if (filterParam.getMiddleFilterParam() == FilterParam.MiddleFilterParam.INTERCEPT) {
                break;
            }

            // 如果上一个过滤器放行了，那么就继续执行
            filterParam = raffleFilter.filter(filterParam);
        }

        // 执行路由，根据过滤参数，调度到指定的规则
        filterParam = filterRouter.filterRouter(filterParam);

        // +++++++++++++++++++++++++
        // +++++ 执行后置过滤器链 +++++
        // +++++++++++++++++++++++++
        for (RaffleFilter raffleFilter : afterFilters) {
            // 如果上一个过滤器拦截了，那么就不再继续执行
            if (filterParam.getMiddleFilterParam() == FilterParam.MiddleFilterParam.INTERCEPT) {
                break;
            }

            // 如果上一个过滤器放行了，那么就继续执行
            filterParam = raffleFilter.filter(filterParam);
        }

        return filterParam;
    }

}