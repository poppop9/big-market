package app.xlog.ggbond.strategy.service.filter;

import app.xlog.ggbond.strategy.model.vo.FilterParam;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import app.xlog.ggbond.user.service.IUserService;
import jakarta.annotation.Resource;
import app.xlog.ggbond.user.service.IUserService;

public class RaffleTimesRaffleFilter implements RaffleFilter {

    @Resource
    private IUserService userService;

    @Resource
    private IStrategyRepository strategyRepository;

    /**
     * 根据用户的抽奖次数，过滤出对应的抽奖池大小
     **/
    @Override
    public FilterParam filter(FilterParam filterParam) {
        Integer raffleTimes = userService.queryRaffleTimesByUserId(filterParam.getUserId());

        // todo 次数写死了
        if (raffleTimes < 10) {
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterParam.setDispatchParam(FilterParam.DispatchParam.LockAwards);

            return filterParam;
        } else if (raffleTimes < 20) {
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterParam.setDispatchParam(FilterParam.DispatchParam.LockLongAwards);

            return filterParam;
        } else if (raffleTimes < 49) {
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterParam.setDispatchParam(FilterParam.DispatchParam.CommonAwards);

            return filterParam;
        } else if (raffleTimes == 49) {
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterParam.setDispatchParam(FilterParam.DispatchParam.GrandAward);

            return filterParam;
        }

        // 基本不可能会走到这里 ……
        filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);

        return filterParam;
    }
}
