package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;

import java.util.List;

/**
 * 安全领域 - 安全服务接口
 */
public interface ISecurityService {

    // 登录
    Boolean doLogin(Long userId, String password);


    // 查询 - 获取当前登录用户id
    Long getLoginIdDefaultNull();

    // 查询 - 判断用户是否是黑名单用户
    Boolean isBlacklistUser(Long userId);

    // 查询 - 查询出所有的黑名单用户
    List<UserBO> queryAllBlacklistUser();

    // 查询 - 跟据userId，查询当前用户
    UserBO findUserByUserId(Long userId);

    // 插入 - 将黑名单用户放入布隆过滤器
    void insertBlacklistUserListToBloomFilter(List<Long> userIds);

}
