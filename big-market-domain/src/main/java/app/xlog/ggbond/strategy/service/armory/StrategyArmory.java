package app.xlog.ggbond.strategy.service.armory;

import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.FormatFlagsConversionMismatchException;
import java.util.List;
import java.util.stream.Stream;


@Service
public class StrategyArmory implements IStrategyArmory {
    @Autowired
    private IStrategyRepository strategyRepository;


    @Override
    public void assembleLotteryStrategy(int strategyId) {
        // 1.查询对应策略的所有奖品
        List<AwardBO> awardBOs = strategyRepository.queryAwards(strategyId);

        // 2. 将awards的awardId和awardRate封装成一个WeightRandom对象
        List<WeightRandom.WeightObj<Integer>> weightObjs = awardBOs.stream()
                // 操作每一个AwardBO，将AwardId，和AwardRate封装成WeightObj对象
                .map(AwardBO -> {
                    return new WeightRandom.WeightObj<Integer>(AwardBO.getAwardId(),
                            AwardBO.getAwardRate());
                }).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);

        // 3. 将WeightRandom对象存入redis
        strategyRepository.insertWeightRandom(strategyId, wr);
    }

    @Override
    public Integer getRandomAwardId(int strategyId) {
        // 拿到redis中的WeightRandom对象
        WeightRandom<Integer> wr = strategyRepository.queryWeightRandom(strategyId);
        return wr.next();
    }
}
