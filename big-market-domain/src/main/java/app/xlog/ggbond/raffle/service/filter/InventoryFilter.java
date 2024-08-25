package app.xlog.ggbond.raffle.service.filter;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import app.xlog.ggbond.raffle.service.armory.IRaffleDispatch;
import app.xlog.ggbond.raffle.service.armory.RaffleArmoryDispatch;
import app.xlog.ggbond.raffle.service.filter.router.FilterRouter;
import app.xlog.ggbond.raffle.service.filter.router.IFilterRouter;
import app.xlog.ggbond.raffle.utils.SpringContextUtil;

// 这个一个后置过滤器，在已经抽奖完成了，知道奖品是什么了，才会进入到这里
// 这个过滤器不需要设置DispatchParam，因为如果库存不足，要去重新调度规则，所以不能覆盖原有的
public class InventoryFilter implements RaffleFilter {

    private final IFilterRouter filterRouter = new FilterRouter();
    private final IRaffleDispatch raffleDispatch;
    private final IAwardInventoryRepository awardInventoryRepository;

    public InventoryFilter() {
        raffleDispatch = SpringContextUtil.getBean(IRaffleDispatch.class);
        awardInventoryRepository = SpringContextUtil.getBean(IAwardInventoryRepository.class);
    }

    @Override
    public FilterParam filter(FilterParam filterParam) {
        // 调度扣减方法
        Boolean result = raffleDispatch.decreaseAwardCount(filterParam.getStrategyId(), filterParam.getAwardId());
        if (!result) {
            // 扣减库存失败，拦截，然后重新调度
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterRouter.filterRouter(filterParam);

            // 将该库存为0的奖品，从缓存的奖品池里移除
            raffleDispatch.removeAwardFromPools(filterParam.getStrategyId(), filterParam.getAwardId());
        }

        // 扣减库存成功，放行
        filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);

        // 将扣减信息写入队列
        awardInventoryRepository.addDecrAwardCountToQueue(
                DecrQueueVO.builder()
                        .strategyId(filterParam.getStrategyId())
                        .awardId(filterParam.getAwardId())
                        .build()
        );

        return filterParam;
    }
}
