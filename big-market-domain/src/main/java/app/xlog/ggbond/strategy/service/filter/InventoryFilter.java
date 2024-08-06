package app.xlog.ggbond.strategy.service.filter;

import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.model.vo.FilterParam;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import app.xlog.ggbond.strategy.service.armory.IStrategyDispatch;
import app.xlog.ggbond.strategy.service.filter.router.IFilterRouter;
import app.xlog.ggbond.strategy.utils.SpringContextUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

// 这个一个后置过滤器，在已经抽奖完成了，知道奖品是什么了，才会进入到这里
// 这个过滤器不需要设置DispatchParam，因为如果库存不足，要去重新调度规则，所以不能覆盖原有的
public class InventoryFilter implements RaffleFilter {

    private IStrategyRepository strategyRepository;
    private IFilterRouter filterRouter;
    private IStrategyDispatch strategyDispatch;

    public InventoryFilter() {
        strategyRepository = SpringContextUtil.getBean(IStrategyRepository.class);
        filterRouter = SpringContextUtil.getBean(IFilterRouter.class);
        strategyDispatch = SpringContextUtil.getBean(IStrategyDispatch.class);
    }

    @Override
    public FilterParam filter(FilterParam filterParam) {
        // 调度扣减方法
        Boolean result = strategyDispatch.decreaseAwardCount(filterParam.getStrategyId(), filterParam.getAwardId());
        if (!result) {
            // 扣减库存失败，拦截，然后重新调度
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterRouter.filterRouter(filterParam);

            // 将该库存为0的奖品从奖品池里移除
            strategyDispatch.removeAwardFromPools(filterParam.getStrategyId(), filterParam.getAwardId());
        }

        // 扣减库存成功，放行
        filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);

        // todo 将扣减信息写入队列，缓慢更新数据库的库存数
        strategyRepository.addDecrAwardCountToQueue(filterParam.getStrategyId(), filterParam.getAwardId());

        return filterParam;
    }
}
