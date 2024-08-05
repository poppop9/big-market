package app.xlog.ggbond.strategy.service.armory;

/*
装配策略的兵工厂，负责初始化策略奖品 ……
 */

public interface IStrategyArmory {
    // 传入一个策略ID，装配所有奖品，由于要还要拼接rule的名字，所以改成String格式
    void assembleLotteryStrategyRuleCommon(Integer strategyId);

    // 传入一个策略ID，装配除去锁出的四个奖品的其余奖品
    void assembleLotteryStrategyRuleLock(Integer strategyId);

    // 传入一个策略ID，装配除去最后一个奖品的其余奖品
    void assembleLotteryStrategyRuleLockLong(Integer strategyId);

    // 传入一个策略ID，装配大奖的抽奖池
    void assembleLotteryStrategyRuleGrand(Integer strategyId);

    // 传入一个策略ID，装配所有奖品的库存
    void assembleLotteryStrategyAwardCount(Integer strategyId);

}
