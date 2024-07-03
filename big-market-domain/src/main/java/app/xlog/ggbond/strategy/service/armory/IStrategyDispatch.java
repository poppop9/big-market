package app.xlog.ggbond.strategy.service.armory;

/*
策略抽奖调度，给出随机抽奖的接口
 */

public interface IStrategyDispatch {
    // 传入一个策略ID，获取一个随机的奖品ID
    Integer getRandomAwardId(int strategyId);
}
