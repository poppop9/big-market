package app.xlog.ggbond.strategy.service.filter;

import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.model.vo.FilterParam;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import app.xlog.ggbond.strategy.service.filter.router.IFilterRouter;
import jakarta.annotation.Resource;

import java.util.List;

// 这个一个后置过滤器，在已经抽奖完成了，知道奖品是什么了，才会进入到这里
// 这个过滤器不需要设置DispatchParam，因为要去重新调度规则，所以不能覆盖原有的
public class InventoryFilter implements RaffleFilter {

    @Resource
    private IStrategyRepository strategyRepository;
    @Resource
    private IFilterRouter filterRouter;

    @Override
    public FilterParam filter(FilterParam filterParam) {
        List<AwardBO> awardBOS = strategyRepository.queryAwards(filterParam.getStrategyId());
        Integer awardCount = awardBOS.stream()
                .filter(AwardBO -> AwardBO.getAwardId().equals(filterParam.getAwardId()))
                .map(AwardBO::getAwardCount)
                .findFirst()
                .get();

        // 如果库存为0，那就拦截，一直重复调度，直到抽到有库存的奖品
        while (awardCount == 0) {
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterRouter.filterRouter(filterParam);
        }

        filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);
        return filterParam;
    }

}
