package app.xlog.ggbond.user.service;

public interface IUserService {
    /**
     * 判断用户是否是黑名单用户
     **/
    Boolean isBlacklistUser(Long userId);

    /**
     * 查询用户的抽奖次数
     **/
    Long queryRaffleTimesByUserId(Long userId);
}
