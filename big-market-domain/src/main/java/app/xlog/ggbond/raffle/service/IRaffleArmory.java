package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.bo.AwardBO;

import java.util.List;

/**
 * 抽奖领域 - 抽奖的兵工厂
 *
 * - 1.初始化装配各种数据
 * - 2.作为外部查询抽奖信息的接口
 */
public interface IRaffleArmory {

    // 装配 - 根据指定策略id，装配该策略所需的所有权重对象
    void assembleRaffleWeightRandomByStrategyId(Long strategyId);

    // 装配 - 根据指定策略id，装配该策略所需的所有权重对象Map
    void assembleRaffleWeightRandomByStrategyId2(Long strategyId);

    // 装配 - 装配所有奖品的库存Map
    void assembleAllAwardCountByStrategyId(Long strategyId);

    // 查询 - 根据活动id，用户id，查询用户的所有奖品
    List<AwardBO> findAllAwards(Long activityId, Long userId);

}
