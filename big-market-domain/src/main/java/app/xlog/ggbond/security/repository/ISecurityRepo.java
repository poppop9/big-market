package app.xlog.ggbond.security.repository;

import app.xlog.ggbond.security.model.UserBO;

import java.util.List;

/**
 * 用户仓储接口
 */
public interface ISecurityRepo {

    // 登录
    Boolean doLogin(Long userId, String password);

    // 查询 - 判断用户是否为黑名单用户
    Boolean isBlacklistUser(Long userId);

    // 查询 - 根据用户id，查询用户
    UserBO findByUserId(Long userId);

    // 查询 - 查询出所有的黑名单用户
    List<UserBO> queryAllBlacklistUser();

    // 插入 - 将黑名单用户放入布隆过滤器
    void insertBlacklistUserListToBloomFilter(List<Long> userIds);

}
