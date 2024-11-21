package app.xlog.ggbond.raffle.service.armory;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
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
        // 对所有规则进行分类，Map<规则，奖品id集合>
        Map<RafflePoolBO.RuleKey, ArrayList<Long>> listMap = raffleRepository.findByRuleTypeAndStrategyOrAwardIdOrderByRuleGradeAsc(strategyId).stream()
                .collect(Collectors.toMap(
                        RafflePoolBO::getRuleKey,
                        item -> new ArrayList<>(List.of(item.getAwardId())),
                        (exist, replace) -> {
                            exist.addAll(replace);
                            return exist;
                        }
                ));
        // 所有奖品
        List<AwardBO> allAwardList = raffleRepository.findAwardsByStrategyId(strategyId);

        // 装配所有奖品的权重对象
        List<WeightRandom.WeightObj<Long>> weightObjs = allAwardList.stream()
                .map(AwardBO -> new WeightRandom.WeightObj<>(AwardBO.getAwardId(), AwardBO.getAwardRate()))
                .toList();
        raffleRepository.insertWeightRandom(strategyId, "all", RandomUtil.weightRandom(weightObjs));

        // 按照规则装配权重对象
        for (Map.Entry<RafflePoolBO.RuleKey, ArrayList<Long>> ruleEntry : listMap.entrySet()) {
            raffleRepository.insertWeightRandom(
                    strategyId,
                    ruleEntry.getKey().getValue(),
                    RandomUtil.weightRandom(allAwardList.stream()
                            .filter(item -> ruleEntry.getValue().contains(item.getAwardId()))
                            .map(AwardBO -> new WeightRandom.WeightObj<>(AwardBO.getAwardId(), AwardBO.getAwardRate()))
                            .toList()
                    )
            );
        }
    }

    @Override
    public void assembleLotteryStrategyRuleLock(Long strategyId) {
        List<WeightRandom.WeightObj<Long>> weightObjs = raffleRepository.queryRuleLockAwards(strategyId).stream()
                .map(AwardBO -> new WeightRandom.WeightObj<>(AwardBO.getAwardId(), AwardBO.getAwardRate()))
                .toList();

        // 将新的WeightRandom对象存入redis，方便后续抽奖调用
        raffleRepository.insertWeightRandom(strategyId, "Lock", RandomUtil.weightRandom(weightObjs));
        log.atInfo().log("抽奖领域 - 装配策略 {} 的 rule_lock 奖品完成", strategyId);
    }

    @Override
    public void assembleLotteryStrategyRuleLockLong(Long strategyId) {
        List<WeightRandom.WeightObj<Long>> weightObjs = raffleRepository.queryRuleLockLongAwards(strategyId).stream()
                .map(AwardBO -> new WeightRandom.WeightObj<>(AwardBO.getAwardId(), AwardBO.getAwardRate()))
                .toList();

        raffleRepository.insertWeightRandom(
                strategyId,
                "LockLong",
                RandomUtil.weightRandom(weightObjs)
        );
        log.atInfo().log("抽奖领域 - 装配策略 {} 的 rule_lock_long 奖品完成", strategyId);
    }

    @Override
    public void assembleLotteryStrategyRuleGrand(Long strategyId) {
        List<WeightRandom.WeightObj<Long>> weightObjs = raffleRepository.queryRuleGrandAwards(strategyId).stream()
                .map(AwardBO -> new WeightRandom.WeightObj<>(AwardBO.getAwardId(), AwardBO.getAwardRate()))
                .toList();

        raffleRepository.insertWeightRandom(
                strategyId,
                "Grand",
                RandomUtil.weightRandom(weightObjs)
        );
        log.atInfo().log("抽奖领域 - 装配策略 {} 的 rule_grand 奖品完成", strategyId);
    }


    @Override
    public void assembleLotteryStrategyAwardCount(Long strategyId) {
        raffleRepository.assembleAwardsCount(strategyId);
    }

    /**
     * 以下是调度
     **/
    // 根据策略ID，获取对应所有奖品中的随机奖品
    @Override
    public Long getRuleCommonAwardIdByRandom(Long strategyId) {
        // 拿到redis中的WeightRandom对象
        WeightRandom<Long> wr = raffleRepository.queryRuleCommonWeightRandom(strategyId);
        return wr.next();
    }

    // 根据策略ID，获取对应除去锁定奖品中的随机奖品
    @Override
    public Long getRuleLockAwardIdByRandom(Long strategyId) {
        WeightRandom<Long> wr = raffleRepository.queryRuleLockWeightRandom(strategyId);
        return wr.next();
    }

    @Override
    public Long getRuleLockLongAwardIdByRandom(Long strategyId) {
        WeightRandom<Long> wr = raffleRepository.queryRuleLockLongWeightRandom(strategyId);
        return wr.next();
    }

    @Override
    public Long getWorstAwardId(Long strategyId) {
        AwardBO awardBO = raffleRepository.queryWorstAwardId(strategyId);
        if (awardBO == null) {
            return null;
        }
        return awardBO.getAwardId();
    }

    @Override
    public Long getRuleGrandAwardIdByRandom(Long strategyId) {
        WeightRandom<Long> wr = raffleRepository.queryRuleGrandAwardIdByRandom(strategyId);
        return wr.next();
    }

}
