package app.xlog.ggbond.strategy.repository;

/*
策略仓库接口，被infrastructure层引入
 */

import app.xlog.ggbond.strategy.model.AwardBO;

import java.util.List;

public interface IStrategyRepository {

    // 查询对应策略的所有奖品
    List<AwardBO> queryAwards(int strategyId);
}
