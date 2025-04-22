package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import cn.hutool.core.lang.WeightRandom;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 抽奖领域 - 调度实现类
 */
@Slf4j
@Service
// @DubboService
public class RaffleDispatch implements IRaffleDispatch {

    @Resource
    private FlowExecutor flowExecutor;
    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;
    @Resource
    private IRaffleDispatchRepo raffleDispatchRepo;

    /**
     * 调度 - 根据策略ID，指定的调度参数，获取对应抽奖池中的随机奖品
     */
    @Override
    public Long findAwardIdByDispatchParam(Long strategyId, RaffleFilterContext.DispatchParam dispatchParam) {
        WeightRandom<Long> weightRandom = raffleArmoryRepo.findWeightRandom2(strategyId, dispatchParam.toString());
        return weightRandom.next();
    }

    /**
     * 调度 - 根据策略id，抽取奖品
     */
    @Override
    @Transactional
    @SneakyThrows
    public RaffleFilterContext raffle(RaffleFilterContext context) {
        context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS);
        Long userId = context.getUserBO().getUserId();

        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("RAFFLE_PRE_AND_ROUTER_CHAIN", null, context);
        if (!liteflowResponse.isSuccess()) {
            log.error("抽奖领域 - " + "用户 " + userId + " 抽奖出现错误");
            throw liteflowResponse.getCause();
        }
        context = liteflowResponse.getContextBean(RaffleFilterContext.class);
        raffleDispatchRepo.sendExecuteRaffleAfterFiltersMessage(context);
        return context;
    }

    /**
     * 调度 - 加锁
     */
    @Override
    public void acquireRaffleLock(Long userId) {
        raffleDispatchRepo.acquireRaffleLock(userId);
    }

    /**
     * 调度 - 释放锁
     */
    @Override
    public void releaseRaffleLock(Long userId) {
        raffleDispatchRepo.releaseRaffleLock(userId);
    }

}
