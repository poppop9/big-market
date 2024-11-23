package app.xlog.ggbond.raffle.service.armory;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;

/**
 * 策略抽奖调度，给出随机抽奖的接口
 */
public interface IRaffleDispatch {

    // 根据策略ID，指定的调度参数，获取对应抽奖池中的随机奖品
    Long findAwardIdByDispatchParam(Long strategyId, RaffleFilterContext.DispatchParam dispatchParam);

}
