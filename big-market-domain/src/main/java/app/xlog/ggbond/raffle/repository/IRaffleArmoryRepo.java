package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import cn.hutool.core.lang.WeightRandom;

import java.util.List;

/**
 * 抽奖领域 - 抽奖的兵工厂仓库:
 * - 1.初始化装配各种数据
 * - 2.作为外部查询抽奖信息的接口
 */
public interface IRaffleArmoryRepo {

    // 查询 - 根据策略id，查询对应的所有奖品
    List<AwardBO> findAwardsByStrategyId(Long strategyId);

    // 查询 - 根据奖品Id，查询对应的奖品
    AwardBO findAwardByAwardId(Long awardId);

    // 查询 - 根据策略Id，查询对应的所有抽奖池规则
    List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId);

    // ---------------------------------------------------

    // 装配 - 装配所有奖品的库存
    void assembleAllAwardCountBystrategyId(Long strategyId);

    // 装配 - 将权重对象插入到Redis中，dispatchParam是抽奖池的名称
    void insertWeightRandom(Long strategyId, String dispatchParam, WeightRandom<Long> wr);

}
