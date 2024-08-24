package app.xlog.ggbond.raffle.repository;

/*
策略仓库接口，被infrastructure层引入
 */

import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.model.StrategyBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import cn.hutool.core.lang.WeightRandom;

import java.util.List;

public interface IRaffleRepository {
    /**
     * 装配策略 ---------------------------------------------------------
     **/
    // 根据策略Id，装配对应的策略
    StrategyBO queryStrategys(Integer strategyId);

    /**
     * 装配，查询奖品-----------------------------------------------------------
     **/
    // 查询对应策略的所有奖品，并缓存到redis
    List<AwardBO> queryCommonAwards(int strategyId);

    // 根据策略ID，查询锁定奖品
    List<AwardBO> queryRuleLockAwards(int strategyId);

    List<AwardBO> queryRuleLockLongAwards(int strategyId);

    AwardBO queryWorstAwardId(Integer strategyId);

    List<AwardBO> queryRuleGrandAwards(Integer strategyId);

    void assembleAwardsCount(Integer strategyId);


    /**
     * 装配权重对象--------------------------------------------------------------
     **/
    // 将权重对象插入到Redis中，awardRule是奖品规则
    void insertWeightRandom(int strategyId, String awardRule, WeightRandom<Integer> wr);

    void updateWeightRandom(int strategyId, String awardRule, WeightRandom<Integer> wr);

    // 从redis中取出所有奖品的权重对象
    WeightRandom<Integer> queryRuleCommonWeightRandom(int strategyId);

    // 从redis中取出除去锁定奖品的权重对象
    WeightRandom<Integer> queryRuleLockWeightRandom(int strategyId);

    WeightRandom<Integer> queryRuleLockLongWeightRandom(Integer strategyId);

    WeightRandom<Integer> queryRuleGrandAwardIdByRandom(Integer strategyId);

}
