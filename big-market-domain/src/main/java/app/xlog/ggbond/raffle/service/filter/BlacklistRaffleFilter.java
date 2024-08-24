package app.xlog.ggbond.raffle.service.filter;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.user.service.IUserService;
import app.xlog.ggbond.user.service.UserService;

// 根据用户id判断是否是黑名单用户
public class BlacklistRaffleFilter implements RaffleFilter {

    private final IUserService userService = new UserService();

    @Override
    public FilterParam filter(FilterParam filterParam) {
        // 如果是黑名单用户，设置FilterParam中的MiddleFilterParam，并设置FilterParam的DispatchParam
        if (userService.isBlacklistUser(filterParam.getUserId())) {
            filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.INTERCEPT);
            filterParam.setDispatchParam(FilterParam.DispatchParam.BlacklistAward);

            return filterParam;
        }

        // 如果不是黑名单用户，放行
        filterParam.setMiddleFilterParam(FilterParam.MiddleFilterParam.PASS);

        return filterParam;
    }
}
