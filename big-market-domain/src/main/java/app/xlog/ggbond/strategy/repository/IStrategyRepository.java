package app.xlog.ggbond.strategy.repository;

/*
策略仓库接口，被infrastructure层引入
 */

import app.xlog.ggbond.strategy.model.AwardBO;
import cn.hutool.core.lang.WeightRandom;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IStrategyRepository {
    // 查询对应策略的所有奖品，并缓存到redis
    List<AwardBO> queryAwards(int strategyId,String rule);

    // 根据策略ID，查询锁定奖品
    List<AwardBO> queryRuleLockAwards(int strategyId,String rule);

    List<AwardBO> queryRuleLockLongAwards(int strategyId, String rule);

    AwardBO queryWorstAwardId(Integer strategyId);

    List<AwardBO> queryRuleGrandAwards(Integer strategyId, String rule);


    // 将权重对象插入到Redis中，awardRule是奖品规则
    void insertWeightRandom(int strategyId, WeightRandom<Integer> wr, String awardRule);

    // 从redis中取出所有奖品的权重对象
    WeightRandom<Integer> queryRuleCommonWeightRandom(int strategyId);

    // 从redis中取出除去锁定奖品的权重对象
    WeightRandom<Integer> queryRuleLockWeightRandom(int strategyId);

    WeightRandom<Integer> queryRuleLockLongWeightRandom(Integer strategyId);

    WeightRandom<Integer> queryRuleGrandAwardIdByRandom(Integer strategyId);

}
