package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;

/**
 * 抽奖领域 - 抽奖调度功能
 */
public interface IRaffleDispatch {

    // 根据策略ID，指定的调度参数，获取对应抽奖池中的随机奖品
    Long findAwardIdByDispatchParam(Long strategyId, RaffleFilterContext.DispatchParam dispatchParam);

    // 根据策略id，抽取奖品
    Long getAwardByStrategyId(Long userId, Long strategyId);

}
