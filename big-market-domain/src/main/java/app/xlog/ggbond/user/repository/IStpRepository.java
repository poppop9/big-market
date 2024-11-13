package app.xlog.ggbond.user.repository;

/**
 * 用户仓储接口
 */
public interface IStpRepository {

    // 登录
    Boolean doLogin(Long userId, String password);

    // 判断当前登录用户，是否为黑名单用户
    Boolean isBlacklistUser(Long userId);

}
