package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.StrategyBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleHistoryBO;

import java.util.List;

/**
 * 抽奖领域 - 抽奖的兵工厂
 *
 * - 1.初始化装配各种数据
 * - 2.作为外部查询抽奖信息的接口
 */
public interface IRaffleArmory {

    // 装配 - 根据指定策略id，装配该策略所需的所有权重对象Map
    void assembleRaffleWeightRandomByStrategyId2(Long strategyId);

    // 装配 - 装配所有奖品的库存Map
    void assembleAllAwardCountByStrategyId(Long strategyId);

    // 装配 - 装配奖品列表
    void assembleAwardList(Long strategyId);

    // 查询 - 根据活动id，用户id，查询用户的所有奖品
    List<AwardBO> findAllAwards(Long activityId, Long userId);

    // 插入 - 将该用户的所有奖品信息插入到数据库
    StrategyBO insertAwardList(Long userId, long activityId, List<AwardBO> awardBOS);

    // 查询 - 查询用户某个活动的中奖奖品信息
    List<UserRaffleHistoryBO> findWinningAwardsInfo(Long activityId, Long userId);

    // 查询 - 跟据活动id，用户id，查询用户的策略id
    Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId);

    // 查询 - 查询用户的抽奖次数
    Long queryRaffleTimesByUserId(Long userId, Long strategyId);

    // 插入 - 插入用户抽奖配置
    void insertUserRaffleConfig(Long userId, long activityId, Long strategyId);

    // 查询 - 查询奖品信息
    AwardBO findAwardByAwardId(Long awardId);
}
