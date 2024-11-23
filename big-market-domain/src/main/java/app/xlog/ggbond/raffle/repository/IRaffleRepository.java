package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.StrategyBO;
import cn.hutool.core.lang.WeightRandom;

import java.util.List;

/**
 * 策略仓储接口
 **/
public interface IRaffleRepository {
    /**
     * ---------------------------
     * ----------- 装配 ----------
     * ---------------------------
     */
    // 将权重对象插入到Redis中，dispatchParam是抽奖池的名称
    void insertWeightRandom(Long strategyId, String dispatchParam, WeightRandom<Long> wr);

    // 从 redis 中查询出指定的权重对象
    WeightRandom<Long> findWeightRandom(Long strategyId, String dispatchParam);

    // 装配所有奖品的库存
    void assembleAllAwardCountBystrategyId(Long strategyId);

    /**
     * ---------------------------
     * ----------- 奖品 ----------
     * ---------------------------
     */
    // 根据策略id，查询对应的所有奖品
    List<AwardBO> findAwardsByStrategyId(Long strategyId);

    // 根据奖品Id，查询对应的奖品
    AwardBO findAwardByAwardId(Long awardId);

    /**
     * ------------------------------
     * ------------ 抽奖池 -----------
     * ------------------------------
     */
    // 根据策略Id，查询对应的所有抽奖池规则
    List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId);

}
