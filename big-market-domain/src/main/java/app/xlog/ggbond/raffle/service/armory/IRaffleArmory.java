package app.xlog.ggbond.raffle.service.armory;

/**
 * 装配策略的兵工厂，负责初始化策略奖品
 */
public interface IRaffleArmory {

    // 根据指定策略id，装配该策略所需的所有权重对象
    void assembleRaffleWeightRandomByStrategyId(Long strategyId);

/*    void assembleLotteryStrategyRuleCommon(Long strategyId);

    void assembleLotteryStrategyRuleLock(Long strategyId);

    void assembleLotteryStrategyRuleLockLong(Long strategyId);

    void assembleLotteryStrategyRuleGrand(Long strategyId);*/

    // 装配所有奖品的库存
    void assembleLotteryStrategyAwardCount(Long strategyId);

}
