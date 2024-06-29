package app.xlog.ggbond.strategy.service.armory;

/*
装配策略的兵工厂，负责初始化策略计算
 */

public interface IStrategyArmory {
    // 传入一个策略ID，装配策略
    void assembleLotteryStrategy(int strategyId);
}
