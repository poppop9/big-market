package app.xlog.ggbond.raffle.service.armory;

import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RaffleArmoryDispatch implements IRaffleArmory, IRaffleDispatch {

    @Resource
    private IRaffleRepository raffleRepository;
    @Resource
    private IAwardInventoryRepository awardInventoryRepository;

    /*
        - 查询对应策略的对应奖品，并缓存到redis。
        - 装配三个抽奖策略的权重对象到redis：
            - rule_common：所有奖品都可以抽
            - rule_lock：锁出后四个奖品，5个奖品可以抽
            - rule_lock_long：锁出最后一个奖品，8个奖品可以抽
     */
    @Override
    public void assembleLotteryStrategyRuleCommon(Integer strategyId) {
        // 1.查询对应策略的所有奖品，并缓存到redis
        List<AwardBO> awardBOs = raffleRepository.queryCommonAwards(strategyId);

        // 2. 将awards的awardId和awardRate封装成一个WeightRandom对象
        List<WeightRandom.WeightObj<Integer>> weightObjs = awardBOs.stream()
                // 操作每一个AwardBO，将AwardId，和AwardRate封装成WeightObj对象
                .map(AwardBO -> new WeightRandom.WeightObj<>(
                        AwardBO.getAwardId(),
                        AwardBO.getAwardRate())
                ).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);

        // 3. 将WeightRandom对象存入redis
        raffleRepository.insertWeightRandom(strategyId, "Common", wr);
        log.atInfo().log("装配策略 {} 的 rule_common 奖品完成", strategyId);
    }

    @Override
    public void assembleLotteryStrategyRuleLock(Integer strategyId) {
        // 拿到除去锁定的所有的奖品
        List<AwardBO> awardRuleLockBOS = raffleRepository.queryRuleLockAwards(strategyId);

        // 生成WeightRandom对象，存入redis中
        List<WeightRandom.WeightObj<Integer>> weightObjs = awardRuleLockBOS.stream()
                // 操作每一个AwardBO，将AwardId，和AwardRate封装成WeightObj对象
                .map(AwardBO -> {
                    return new WeightRandom.WeightObj<>(AwardBO.getAwardId(),
                            AwardBO.getAwardRate());
                }).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);

        // 将新的WeightRandom对象存入redis，方便后续抽奖调用
        raffleRepository.insertWeightRandom(strategyId, "Lock", wr);
        log.atInfo().log("装配策略 {} 的 rule_lock 奖品完成", strategyId);
    }

    @Override
    public void assembleLotteryStrategyRuleLockLong(Integer strategyId) {
        List<AwardBO> awardRuleLockLongBOS = raffleRepository.queryRuleLockLongAwards(strategyId);

        List<WeightRandom.WeightObj<Integer>> weightObjs = awardRuleLockLongBOS.stream()
                .map(AwardBO -> {
                    return new WeightRandom.WeightObj<>(AwardBO.getAwardId(),
                            AwardBO.getAwardRate());
                }).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);

        raffleRepository.insertWeightRandom(strategyId, "LockLong", wr);
        log.atInfo().log("装配策略 {} 的 rule_lock_long 奖品完成", strategyId);
    }

    @Override
    public void assembleLotteryStrategyRuleGrand(Integer strategyId) {
        List<AwardBO> awardRuleGrandBOS = raffleRepository.queryRuleGrandAwards(strategyId);

        List<WeightRandom.WeightObj<Integer>> weightObjs = awardRuleGrandBOS.stream()
                .map(AwardBO -> {
                    return new WeightRandom.WeightObj<>(AwardBO.getAwardId(),
                            AwardBO.getAwardRate());
                }).toList();

        WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);
        raffleRepository.insertWeightRandom(strategyId, "Grand", wr);
        log.atInfo().log("装配策略 {} 的 rule_grand 奖品完成", strategyId);
    }

    @Override
    public void assembleLotteryStrategyAwardCount(Integer strategyId) {
        // 为什么不命名为query，因为目的不在query，目的在assemble
        raffleRepository.assembleAwardsCount(strategyId);
    }

    /**
     * 以下是调度
     **/
    // 根据策略ID，获取对应所有奖品中的随机奖品
    @Override
    public Integer getRuleCommonAwardIdByRandom(Integer strategyId) {
        // 拿到redis中的WeightRandom对象
        WeightRandom<Integer> wr = raffleRepository.queryRuleCommonWeightRandom(strategyId);
        return wr.next();
    }

    // 根据策略ID，获取对应除去锁定奖品中的随机奖品
    @Override
    public Integer getRuleLockAwardIdByRandom(Integer strategyId) {
        WeightRandom<Integer> wr = raffleRepository.queryRuleLockWeightRandom(strategyId);
        return wr.next();
    }

    @Override
    public Integer getRuleLockLongAwardIdByRandom(Integer strategyId) {
        WeightRandom<Integer> wr = raffleRepository.queryRuleLockLongWeightRandom(strategyId);
        return wr.next();
    }

    @Override
    public Integer getWorstAwardId(Integer strategyId) {
        AwardBO awardBO = raffleRepository.queryWorstAwardId(strategyId);
        if (awardBO == null) {
            return null;
        }
        return awardBO.getAwardId();
    }

    @Override
    public Integer getRuleGrandAwardIdByRandom(Integer strategyId) {
        WeightRandom<Integer> wr = raffleRepository.queryRuleGrandAwardIdByRandom(strategyId);
        return wr.next();
    }

    /**
     * 更新奖品库存
     * @param strategyId
     * @param awardId
     * @return
     */
    @Override
    public Boolean decreaseAwardCount(Integer strategyId, Integer awardId) {
        return awardInventoryRepository.decreaseAwardCount(strategyId, awardId);
    }

    /**
     * 将该奖品从缓存中的所有抽奖池里移除
     */
    @Override
    public void removeAwardFromPools(Integer strategyId, Integer awardId) {
        awardInventoryRepository.removeAwardFromPools(strategyId, awardId);
    }
}
