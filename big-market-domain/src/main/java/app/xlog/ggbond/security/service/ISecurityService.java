package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;

import java.util.List;

/**
 * 安全领域 - 安全服务接口
 *
 * todo 应用一启动，就要登录匿名用户200，将其抽奖信息装配到redis中（这样如果一个没有购买数据的新用户登录进来，就可以使用200用户）
 * todo 200用户的 TTL 是永久
 * todo 不能用 200用户，这样每个用户的库存没法管理
 *
 * todo 索引未完善
 * todo 造一些用户购买历史数据，测试ai生成速度
 * todo 奖品的库存不能在奖品表中，要跟用户绑定，这样就能解耦了（所有用户的最低级别奖品都是随机积分）
 *     - 奖品的库存要放在策略奖品表中
 *     - strategyId 也不能放在奖品表中（因为一个奖品可能会有多个策略使用）
 *     - awardSort 也不能放在奖品表中
 * todo 奖品在前端的排序不能跟据原来的那个字段来了
 * todo 过滤器链对于同一个用户来说，应该要加锁
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

    // 判断 - 检查该用户是否有策略
    boolean existsByUserIdAndActivityId(Long activityId, Long userId);

}
