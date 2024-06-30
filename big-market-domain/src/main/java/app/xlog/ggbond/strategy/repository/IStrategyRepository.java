package app.xlog.ggbond.strategy.repository;

/*
策略仓库接口，被infrastructure层引入
 */

import app.xlog.ggbond.strategy.model.AwardBO;
import cn.hutool.core.lang.WeightRandom;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IStrategyRepository {

    // 查询对应策略的所有奖品
    List<AwardBO> queryAwards(int strategyId);

    // 将权重对象插入到Redis中
    void insertWeightRandom(int strategyId, WeightRandom<Integer> wr);

    // 查询权重对象
    WeightRandom<Integer> queryWeightRandom(int strategyId);
}
