package app.xlog.ggbond.strategy.service.armory;

import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {
    @Autowired
    private IStrategyRepository strategyRepository;

    /*
        我要装配三个抽奖策略：
        - rule_common：所有奖品都可以抽
        - rule_lock：锁出后四个奖品，5个奖品可以抽
        - rule_lock_long：锁出最后一个奖品，8个奖品可以抽
     */

    @Override
    public void assembleLotteryStrategyRuleCommon(Integer strategyId) {
        // 1.查询对应策略的所有奖品，并缓存到redis
        List<AwardBO> awardBOs = strategyRepository.queryAwards(strategyId, "Common");

        // 2. 将awards的awardId和awardRate封装成一个WeightRandom对象
        List<WeightRandom.WeightObj<Integer>> weightObjs = awardBOs.stream()
                // 操作每一个AwardBO，将AwardId，和AwardRate封装成WeightObj对象
                .map(AwardBO -> {
                    return new WeightRandom.WeightObj<>(AwardBO.getAwardId(),
                            AwardBO.getAwardRate());
                }).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);

        // 3. 将WeightRandom对象存入redis
        strategyRepository.insertWeightRandom(strategyId, wr, "Common");
    }

    @Override
    public void assembleLotteryStrategyRuleLock(Integer strategyId) {
        // 拿到除去锁定的所有的奖品
        List<AwardBO> awardRuleLockBOS = strategyRepository.queryRuleLockAwards(strategyId, "Lock");

        // 生成WeightRandom对象，存入redis中
        List<WeightRandom.WeightObj<Integer>> weightObjs = awardRuleLockBOS.stream()
                // 操作每一个AwardBO，将AwardId，和AwardRate封装成WeightObj对象
                .map(AwardBO -> {
                    return new WeightRandom.WeightObj<>(AwardBO.getAwardId(),
                            AwardBO.getAwardRate());
                }).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);

        // 将新的WeightRandom对象存入redis，方便后续抽奖调用
        strategyRepository.insertWeightRandom(strategyId, wr, "Lock");
    }

    @Override
    public void assembleLotteryStrategyRuleLockLong(Integer strategyId) {
        List<AwardBO> awardRuleLockLongBOS = strategyRepository.queryRuleLockLongAwards(strategyId, "LockLong");

        List<WeightRandom.WeightObj<Integer>> weightObjs = awardRuleLockLongBOS.stream()
                .map(AwardBO -> {
                    return new WeightRandom.WeightObj<>(AwardBO.getAwardId(),
                            AwardBO.getAwardRate());
                }).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);

        strategyRepository.insertWeightRandom(strategyId, wr, "LockLong");
    }

    // 根据策略ID，获取对应所有奖品中的随机奖品
    @Override
    public Integer getRuleCommonAwardIdByRandom(Integer strategyId) {
        // 拿到redis中的WeightRandom对象
        WeightRandom<Integer> wr = strategyRepository.queryRuleCommonWeightRandom(strategyId);
        return wr.next();
    }

    // 根据策略ID，获取对应除去锁定奖品中的随机奖品
    @Override
    public Integer getRuleLockAwardIdByRandom(Integer strategyId) {
        WeightRandom<Integer> wr = strategyRepository.queryRuleLockWeightRandom(strategyId);
        return wr.next();
    }

    @Override
    public Integer getRuleLockLongAwardIdByRandom(Integer strategyId) {
        WeightRandom<Integer> wr = strategyRepository.queryRuleLockLongWeightRandom(strategyId);
        return wr.next();
    }
}
