package app.xlog.ggbond.strategy.service.filter;

import app.xlog.ggbond.strategy.model.vo.FilterParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
// 抽奖过滤器链，用于定义抽奖过滤器的数量和执行顺序
public class RaffleFilterChain {
    private final List<RaffleFilter> filters = new ArrayList<>();

    /**
     * 添加过滤器
     **/
    public void addFilter(RaffleFilter raffleFilter) {
        filters.add(raffleFilter);
    }

    /**
     * 执行一整条过滤器链
     **/
    public FilterParam doFilter(FilterParam filterParam) {

        for (RaffleFilter raffleFilter : filters) {
            // 如果上一个过滤器拦截了，那么就不再继续执行
            if (filterParam.getMiddleFilterParam() == FilterParam.MiddleFilterParam.INTERCEPT) {
                break;
            }

            filterParam = raffleFilter.filter(filterParam);
        }

        return filterParam;
    }
}