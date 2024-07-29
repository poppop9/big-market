package app.xlog.ggbond.strategy.service.filter;

import app.xlog.ggbond.strategy.model.vo.FilterParam;
import com.fasterxml.jackson.core.JsonProcessingException;

// 抽奖过滤器接口
public interface RaffleFilter {
    FilterParam filter(FilterParam filterParam);
}
