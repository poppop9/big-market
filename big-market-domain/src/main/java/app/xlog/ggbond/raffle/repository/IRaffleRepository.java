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
     * --------- 装配策略 ---------
     * ---------------------------
     */
    // 根据策略Id，装配对应的策略
    StrategyBO findStrategyByStrategyId(Long strategyId);


    /**
     * ---------------------------
     * --------- 查询奖品 ---------
     * ---------------------------
     */
    // 根据策略id，查询对应的所有奖品
    List<AwardBO> findAwardsByStrategyId(Long strategyId);

    // 根据奖品Id，查询对应的奖品
    AwardBO findAwardByAwardId(Long awardId);

    List<AwardBO> queryRuleLockLongAwards(Long strategyId);

    AwardBO queryWorstAwardId(Long strategyId);

    List<AwardBO> queryRuleGrandAwards(Long strategyId);

    // 装配所有奖品的库存
    void assembleAllAwardCountBystrategyId(Long strategyId);

    /**
     * ------------------------------
     * --------- 查询抽奖池规则 ---------
     * ------------------------------
     */
    // 根据策略Id，查询对应的所有抽奖池规则
    List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId);

    /**
     * ---------------------------
     * --------- 装配权重对象 ------
     * ---------------------------
     */
    // 将权重对象插入到Redis中，awardRule是奖品规则
    void insertWeightRandom(Long strategyId, String awardRule, WeightRandom<Long> wr);

    void updateWeightRandom(Long strategyId, String awardRule, WeightRandom<Long> wr);

    // 从 redis 中查询出指定的权重对象
    WeightRandom<Long> findWeightRandom(Long strategyId, String dispatchParam);

}
