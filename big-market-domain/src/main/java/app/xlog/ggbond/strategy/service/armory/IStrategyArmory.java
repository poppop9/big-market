package app.xlog.ggbond.strategy.service.armory;

/*
装配策略的兵工厂，负责初始化策略奖品 ……
 */

public interface IStrategyArmory {
    // 因为有三个方法，这是内部的装配封装操作
    void assembleLotteryStrategy(int strategyId);

    // 传入一个策略ID，装配所有奖品
    void assembleLotteryStrategyRuleCommon(int strategyId);

    // 传入一个策略ID，装配除去锁出的四个奖品的其余奖品
    void assembleLotteryStrategyRuleLock(int strategyId);

    // 传入一个策略ID，装配除去最后一个奖品的其余奖品
    void assembleLotteryStrategyRuleLockLong(int strategyId);

}
