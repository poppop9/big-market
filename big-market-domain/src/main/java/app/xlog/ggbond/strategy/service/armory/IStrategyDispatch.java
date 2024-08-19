package app.xlog.ggbond.strategy.service.armory;

/*
策略抽奖调度，给出随机抽奖的接口
 */

public interface IStrategyDispatch {
    // 传入一个策略ID，获取一个所有奖品 101-109 中的随机奖品ID
    Integer getRuleCommonAwardIdByRandom(Integer strategyId);

    // 传入一个策略ID，获取一个除去锁定奖品 101-105 的随机奖品ID
    Integer getRuleLockAwardIdByRandom(Integer strategyId);

    // 传入一个策略ID，获取一个除去最后一个奖品 101-108 的随机奖品ID
    Integer getRuleLockLongAwardIdByRandom(Integer strategyId);

    // 传入一个策略ID，获取最次的一个奖品，给黑名单用户准备的
    Integer getWorstAwardId(Integer strategyId);

    // 传入一个策略ID，获取大奖池里的一个奖品
    Integer getRuleGrandAwardIdByRandom(Integer strategyId);

    // 传入一个策略ID，奖品ID，扣减对应奖品的库存
    Boolean decreaseAwardCount(Integer strategyId, Integer awardId);

    // 传入一个策略ID，奖品ID，将该奖品从缓存中的所有抽奖池里移除
    void removeAwardFromPools(Integer strategyId, Integer awardId);
}
