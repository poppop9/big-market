package app.xlog.ggbond.security.service;

public interface ISecurityService {

    // 判断用户是否是黑名单用户
    Boolean isBlacklistUser();

    // 登录
    Boolean doLogin(Long userId, String password);

    // 查询用户的抽奖次数
    Long queryRaffleTimesByUserId(Long userId);
}
