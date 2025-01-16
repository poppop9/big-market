package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;
import cn.hutool.core.lang.WeightRandom;

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

    // 查询 - 权重对象 - 从 redis 中查询出指定策略的所有权重对象
    List<WeightRandom<Long>> findAllWeightRandomByStrategyId(Long strategyId);

    // 查询 - 奖品 - 根据策略id，查询对应的所有奖品
    List<AwardBO> findAwardsByStrategyId(Long strategyId);

    // 查询 - 奖品 - 根据奖品Id，查询对应的奖品
    AwardBO findAwardByAwardId(Long awardId);

    // 查询 - 抽奖池 - 根据策略Id，查询对应的所有抽奖池规则
    List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId);

    // 查询 - 奖品 - 根据活动id，用户id，查询用户的所有奖品
    List<AwardBO> findAllAwards(Long activityId, Long userId);

    // 装配 - 奖品库存 - 装配所有奖品的库存
    void assembleAllAwardCountByStrategyId(Long strategyId);

    // 装配 - 权重对象 - 将权重对象插入到Redis中，dispatchParam是抽奖池的名称
    void insertWeightRandom(Long strategyId, String dispatchParam, WeightRandom<Long> wr);

    // 装配 - 权重对象 - 将权重对象Map插入到Redis中
    void insertWeightRandom(Long strategyId, Map<String, WeightRandom<Long>> wrMap);

    // 插入 - 插入奖品
    List<AwardBO> insertAwardList(List<AwardBO> awardBOS);

    // 插入 - 插入策略
    long insertStrategy(long activityId);

    // 插入 - 插入策略奖品
    void insertStrategyAwardList(long strategyId, List<AwardBO> awardBOS);

    // 插入 - 插入抽奖池
    void insertRafflePoolList(long strategyId, List<AwardBO> awardBOS);

    // 判断 - 用户是否在抽奖中
    boolean isUserInRaffle(Long userId);

    // 修改 - 在BitSet中给用户加锁
    void lockUserInBitSet(Long userId);

    // 修改 - 在BitSet中给用户解锁
    void unLockUserInBitSet(Long userId);
}
