package app.xlog.ggbond.raffle.service.filter;

import app.xlog.ggbond.raffle.model.vo.FilterParam;

// 抽奖过滤器接口
public interface RaffleFilter {
    FilterParam filter(FilterParam filterParam);
}
