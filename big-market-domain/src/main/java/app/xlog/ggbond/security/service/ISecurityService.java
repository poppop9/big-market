package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;

import java.util.List;

/**
 * 安全领域 - 安全服务接口
 * <p>
 * - todo 目前这个抽奖池也不是动态的，最好做到以活动为单位，每个活动的抽奖池配置可以自定义
 * - todo 生成大规模的用户，和历史购买数据
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

    // 查询 - 查询用户的抽奖次数
    Long queryRaffleTimesByUserId(Long userId, Long strategyId);

    // 查询 - 查询用户某个活动的中奖奖品信息
    List<UserRaffleHistoryBO> findWinningAwardsInfo(Long activityId, Long userId);

    // 查询 - 根据活动id，用户id，查询用户的策略id
    Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId);

    // 查询 - 查询用户购买历史
    List<UserPurchaseHistoryBO> findUserPurchaseHistory(Long userId);

    // 插入 - 将黑名单用户放入布隆过滤器
    void insertBlacklistUserListToBloomFilter(List<Long> userIds);

    // 插入 - 将当前用户的角色信息放入session
    void insertPermissionIntoSession();

    // 插入 - 插入用户抽奖配置
    void insertUserRaffleConfig(Long userId, long activityId, Long strategyId);

    // 判断 - 检查该用户是否有策略
    boolean existsByUserIdAndActivityId(Long activityId, Long userId);

}
