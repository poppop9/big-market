package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleHistoryBO;
import cn.hutool.core.lang.WeightRandom;
import org.redisson.api.RBitSet;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;

import java.util.List;
import java.util.Map;

/**
 * 抽奖领域 - 抽奖的兵工厂仓库
 * <p>
 * - 1.初始化装配各种数据
 * - 2.作为外部查询抽奖信息的接口
 */
public interface IRaffleArmoryRepo {

    // 查询 - 权重对象 - 从 redis 中查询出指定的权重对象
    WeightRandom<Long> findWeightRandom(Long strategyId, String dispatchParam);

    // 查询 - 权重对象 - 从 redis 中查询出指定的权重对象
    WeightRandom<Long> findWeightRandom2(Long strategyId, String dispatchParam);

    // 查询 - 奖品 - 根据策略id，查询对应的所有奖品
    List<AwardBO> findAwardsByStrategyId(Long strategyId);

    // 查询 - 奖品 - 根据策略id，奖品Id，查询奖品详情
    AwardBO findAwardByStrategyIdAndAwardId(Long strategyId, Long awardId);

    // 查询 - 抽奖池 - 根据策略Id，查询对应的所有抽奖池规则
    List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId);

    // 查询 - 奖品 - 根据活动id，用户id，查询用户的所有奖品
    List<AwardBO> findAllAwards(Long activityId, Long userId);

    // 装配 - 奖品库存 - 装配所有奖品的库存
    void assembleAllAwardCountByStrategyId(Long strategyId);

    // 装配 - 权重对象 - 将权重对象Map插入到Redis中
    void insertWeightRandom(Long strategyId, Map<String, WeightRandom<Long>> wrMap);

    // 装配 - 权重对象 - 将权重对象Map强制插入到Redis中
    void forceInsertWeightRandom(Long strategyId, Map<String, WeightRandom<Long>> wrMap);

    // 装配 - 装配奖品列表
    void assembleAwardList(Long strategyId);

    // 插入 - 插入奖品
    List<AwardBO> insertAwardList(List<AwardBO> awardBOS);

    // 插入 - 插入策略
    long insertStrategy(long activityId);

    // 插入 - 插入策略奖品
    void insertStrategyAwardList(long strategyId, List<AwardBO> awardBOS);

    // 插入 - 插入抽奖池
    void insertRafflePoolList(long strategyId, List<AwardBO> awardBOS);

    // 修改 - 在BitSet中给用户解锁
    void unLockUserInBitSet(Long userId);

    // 查询 - 跟据活动id，用户id，查询用户的策略id
    Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId);

    // 查询 - 根据用户id，策略id，查询用户的抽奖历史
    List<UserRaffleHistoryBO> getWinningAwardsInfo(Long userId, Long strategyId);

    // 查询 - 查询用户的抽奖次数
    Long queryRaffleTimesByUserId(Long userId, Long strategyId);

    // 插入 - 插入用户抽奖配置
    void insertUserRaffleConfig(Long userId, long activityId, Long strategyId);

    // 查询 - 查询用户的抽奖次数
    Long findRaffleCount(Long activityId, Long userId);

    // 判断 - 是否能获取到抽奖锁
    boolean acquireRaffleLock(Long userId);

    // 判断 - 判断redis中是否有指定的权重对象
    boolean existWeightRandom(Long strategyId);
}
