package app.xlog.ggbond.security.repository;

import app.xlog.ggbond.security.model.UserBO;

/**
 * 用户仓储接口
 */
public interface ISecurityRepository {

    // 登录
    Boolean doLogin(Long userId, String password);

    // 判断用户是否为黑名单用户
    Boolean isBlacklistUser(Long userId);

    // 根据用户id，查询用户
    UserBO findByUserId(Long userId);

}
