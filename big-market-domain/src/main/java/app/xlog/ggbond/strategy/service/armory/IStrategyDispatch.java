package app.xlog.ggbond.strategy.service.armory;

/*
策略抽奖调度，给出随机抽奖的接口
 */

public interface IStrategyDispatch {
    // 传入一个策略ID，获取一个所有奖品中的随机奖品ID
    Integer getRuleCommonAwardIdByRandom(Integer strategyId);

    // 传入一个策略ID，获取一个除去锁定奖品的随机奖品ID
    Integer getRuleLockAwardIdByRandom(Integer strategyId);
}
