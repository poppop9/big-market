package app.xlog.ggbond.security.repository;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;

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

    // 查询 - 跟据活动id，用户id，查询用户的策略id
    Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId);

    // 查询 - 根据用户id，策略id，查询用户的抽奖历史
    List<UserRaffleHistoryBO> getWinningAwardsInfo(Long userId, Long strategyId);

    // 查询 - 查询当前用户的抽奖次数
    Long queryRaffleTimesByUserId(Long userId, Long strategyId);

    // 插入 - 将黑名单用户放入布隆过滤器
    void insertBlacklistUserListToBloomFilter(List<Long> userIds);

}
