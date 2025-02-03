package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.bo.UserBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;

/**
 * 抽奖领域 - 抽奖调度功能
 */
public interface IRaffleDispatch {

    // 调度 - 根据策略ID，指定的调度参数，获取对应抽奖池中的随机奖品
    Long findAwardIdByDispatchParam(Long strategyId, RaffleFilterContext.DispatchParam dispatchParam);

    // 调度 - 抽取奖品
    RaffleFilterContext raffle(RaffleFilterContext context);

}
