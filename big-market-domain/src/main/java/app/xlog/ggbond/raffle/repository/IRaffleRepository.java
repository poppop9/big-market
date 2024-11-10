package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.model.StrategyBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import cn.hutool.core.lang.WeightRandom;

import java.util.List;

/**
 * 策略仓储接口
 **/
public interface IRaffleRepository {
    /**
     * 装配策略 ---------------------------------------------------------
     **/
    // 根据策略Id，装配对应的策略
    StrategyBO queryStrategys(Long strategyId);

    /**
     * 装配，查询奖品-----------------------------------------------------------
     **/
    // 查询对应策略的所有奖品，并缓存到redis
    List<AwardBO> queryCommonAwards(Long strategyId);

    // 根据策略ID，查询锁定奖品
    List<AwardBO> queryRuleLockAwards(Long strategyId);

    List<AwardBO> queryRuleLockLongAwards(Long strategyId);

    AwardBO queryWorstAwardId(Long strategyId);

    List<AwardBO> queryRuleGrandAwards(Long strategyId);

    void assembleAwardsCount(Long strategyId);


    /**
     * 装配权重对象--------------------------------------------------------------
     **/
    // 将权重对象插入到Redis中，awardRule是奖品规则
    void insertWeightRandom(Long strategyId, String awardRule, WeightRandom<Long> wr);

    void updateWeightRandom(Long strategyId, String awardRule, WeightRandom<Long> wr);

    // 从redis中取出所有奖品的权重对象
    WeightRandom<Long> queryRuleCommonWeightRandom(Long strategyId);

    // 从redis中取出除去锁定奖品的权重对象
    WeightRandom<Long> queryRuleLockWeightRandom(Long strategyId);

    WeightRandom<Long> queryRuleLockLongWeightRandom(Long strategyId);

    WeightRandom<Long> queryRuleGrandAwardIdByRandom(Long strategyId);

}
