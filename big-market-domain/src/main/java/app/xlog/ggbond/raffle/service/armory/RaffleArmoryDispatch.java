package app.xlog.ggbond.raffle.service.armory;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RaffleArmoryDispatch implements IRaffleArmory, IRaffleDispatch {

    @Resource
    private IRaffleRepository raffleRepository;

    /**
     * 根据指定策略id，装配该策略所需的所有权重对象
     * todo
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

}
