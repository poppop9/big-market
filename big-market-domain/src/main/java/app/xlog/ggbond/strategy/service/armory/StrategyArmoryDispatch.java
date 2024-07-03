package app.xlog.ggbond.strategy.service.armory;

import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {
    @Autowired
    private IStrategyRepository strategyRepository;


    /*
        我要装配三个抽奖策略：
        - rule_common：所有奖品都可以抽
        - rule_lock：锁出后四个奖品
        - rule_lock_long：锁出最后一个奖品
     */
    // 因为有三个方法，这是内部的装配封装操作
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
    public void assembleLotteryStrategyRuleCommon(int strategyId) {

    }

    @Override
    public void assembleLotteryStrategyRuleLock(int strategyId) {

    }

    @Override
    public void assembleLotteryStrategyRuleLockLong(int strategyId) {

    }

    // 根据策略ID，查对应所有奖品中的随机奖品
    @Override
    public Integer getRandomAwardId(int strategyId) {
        // 拿到redis中的WeightRandom对象
        WeightRandom<Integer> wr = strategyRepository.queryWeightRandom(strategyId);
        return wr.next();
    }
}
