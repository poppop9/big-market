package app.xlog.ggbond.raffle.service.armory;

/**
 * 策略抽奖调度，给出随机抽奖的接口
 */
public interface IRaffleDispatch {

    // 获取一个所有奖品中的随机奖品ID
    Long getRuleCommonAwardIdByRandom(Long strategyId);

    // 获取一个除去锁定奖品的随机奖品ID
    Long getRuleLockAwardIdByRandom(Long strategyId);

    // 获取一个除去最后一个奖品的随机奖品ID
    Long getRuleLockLongAwardIdByRandom(Long strategyId);

    // 获取最次的一个奖品，给黑名单用户准备的
    Long getWorstAwardId(Long strategyId);

    // 获取大奖池里的一个奖品
    Long getRuleGrandAwardIdByRandom(Long strategyId);

}
