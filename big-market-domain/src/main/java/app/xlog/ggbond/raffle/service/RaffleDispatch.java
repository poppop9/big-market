package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.raffle.model.bo.*;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 抽奖领域 - 调度实现类
 */
@Slf4j
@Service
@DubboService
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

        log.atInfo().log("抽奖领域 - " + userId + " 过滤器链开始执行");
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("RAFFLE_FILTER_CHAIN", null, context);
        if (!liteflowResponse.isSuccess()) throw liteflowResponse.getCause();
        log.atInfo().log("抽奖领域 - " + userId + " 过滤器链执行完毕");

        return liteflowResponse.getContextBean(RaffleFilterContext.class);
    }

}
