package app.xlog.ggbond.user.service;

public interface IUserService {

    // 判断用户是否是黑名单用户
    Boolean isBlacklistUser();

    // 查询用户的抽奖次数
    Long queryRaffleTimesByUserId(Long userId);

    // 登录
    Boolean doLogin(Long userId, String password);

}
