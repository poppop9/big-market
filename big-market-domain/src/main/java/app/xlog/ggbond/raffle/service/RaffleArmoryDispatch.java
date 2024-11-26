package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import app.xlog.ggbond.raffle.service.filterChain.RaffleFilterChain;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * 抽奖领域 - 及（装配，查询，调度）于一身的实现类
 */
@Slf4j
@Service
public class RaffleArmoryDispatch implements IRaffleArmory, IRaffleDispatch {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ISecurityService securityService;

    @Resource
    private RaffleFilterChain raffleFilterChain;
    @Resource
    private IRaffleRepository raffleRepository;

    // ------------------------------
    // ------------ 装配 -------------
    // ------------------------------

    /**
     * 根据指定策略id，装配该策略所需的所有权重对象
     */
    @Override
    public void assembleRaffleWeightRandomByStrategyId(Long strategyId) {
        // 所有的权重对象集合
        raffleRepository.findAllRafflePoolByStrategyId(strategyId)
                .forEach(item -> {
                    // 生成权重集合
                    List<WeightRandom.WeightObj<Long>> weightObjs = item.getAwardIds().stream()
                            .map(child -> {
                                AwardBO award = raffleRepository.findAwardByAwardId(child);
                                return new WeightRandom.WeightObj<>(child, award.getAwardRate());
                            })
                            .toList();
                    WeightRandom<Long> WeightRandom = RandomUtil.weightRandom(weightObjs);

                    // 将WeightRandom对象存入redis，方便后续抽奖调用
                    raffleRepository.insertWeightRandom(strategyId, item.getRafflePoolName(), WeightRandom);
                });
    }

    /**
     * 装配所有奖品的库存
     */
    @Override
    public void assembleAllAwardCountBystrategyId(Long strategyId) {
        raffleRepository.assembleAllAwardCountBystrategyId(strategyId);
    }

    // ------------------------------
    // ------------ 查询 -------------
    // ------------------------------

    /**
     * 根据策略id，查询对应的所有奖品
     */
    @Override
    public List<ObjectNode> findAllAwardByStrategyId(Long strategyId) {
        return raffleRepository.findAwardsByStrategyId(strategyId).stream()
                .map(awardBO -> objectMapper.<ObjectNode>valueToTree(awardBO))
                .sorted(Comparator.comparingInt(o -> o.get("awardSort").asInt()))
                .toList();
    }

    // ------------------------------
    // ------------ 调度 -------------
    // ------------------------------

    /**
     * 根据策略ID，指定的调度参数，获取对应抽奖池中的随机奖品
     */
    @Override
    public Long findAwardIdByDispatchParam(Long strategyId, RaffleFilterContext.DispatchParam dispatchParam) {
        WeightRandom<Long> weightRandom = raffleRepository.findWeightRandom(strategyId, dispatchParam.toString());
        return weightRandom.next();
    }

    /**
     * 根据策略id，抽取奖品
     */
    @Override
    public Long getAwardByStrategyId(Long userId, Long strategyId) {
        // 执行过滤器链
        return raffleFilterChain.executeFilterChain(RaffleFilterContext.builder()
                .userId(securityService.getLoginIdDefaultNull())
                .strategyId(strategyId)
                .middleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS)
                .build()
        );
    }

}
