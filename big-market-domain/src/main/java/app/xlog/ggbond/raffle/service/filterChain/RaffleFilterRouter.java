package app.xlog.ggbond.raffle.service.filterChain;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.service.armory.IRaffleDispatch;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RaffleFilterRouter {

    @Resource
    private IRaffleDispatch raffleDispatch;

    public RaffleFilterContext router(RaffleFilterContext context) {
        Long awardId = switch (context.getDispatchParam()) {
            case CommonAwards -> raffleDispatch.getRuleCommonAwardIdByRandom(context.getStrategyId());
            case LockAwards -> raffleDispatch.getRuleLockAwardIdByRandom(context.getStrategyId());
            case LockLongAwards -> raffleDispatch.getRuleLockLongAwardIdByRandom(context.getStrategyId());
            case BlacklistAward -> raffleDispatch.getWorstAwardId(context.getStrategyId());
            case GrandAward -> raffleDispatch.getRuleGrandAwardIdByRandom(context.getStrategyId());
        };

        context.setAwardId(awardId);
        return context;
    }
}
