package app.xlog.ggbond.security.service;

/**
 * 安全领域 - 安全服务接口
 */
public interface ISecurityService {

    // 登录
    Boolean doLogin(Long userId, String password);

    // --------------------------------
    // ------------- 查询 --------------
    // --------------------------------
    // 获取当前登录用户id
    Long getLoginIdDefaultNull();

    // 判断用户是否是黑名单用户
    Boolean isBlacklistUser(Long userId);

    // 查询当前用户的抽奖次数
    Long queryRaffleTimesByUserId(Long userId, Long strategyId);

}
