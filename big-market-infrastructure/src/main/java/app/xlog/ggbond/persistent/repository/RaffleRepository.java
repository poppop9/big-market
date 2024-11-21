package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.persistent.repository.jpa.RaffleRuleRepository;
import app.xlog.ggbond.persistent.repository.jpa.StrategyRepository;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.StrategyBO;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.WeightRandom;
import jakarta.annotation.Resource;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 策略仓库实现类  todo 很多方法需要修改
 */
@Repository
public class RaffleRepository implements IRaffleRepository {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StrategyRepository strategyRepository;
    @Resource
    private AwardRepository awardRepository;
    @Resource
    private RaffleRuleRepository raffleRuleRepository;

    /**
     * 根据策略ID查询策略
     **/
    @Override
    public StrategyBO findStrategyByStrategyId(Long strategyId) {
        RBucket<Object> bucket = redissonClient.getBucket("strategy_" + strategyId);
        if (bucket.isExists()) return (StrategyBO) bucket.get();

        StrategyBO strategyBO = BeanUtil.copyProperties(
                strategyRepository.findByStrategyId(strategyId), StrategyBO.class
        );
        bucket.set(strategyBO);

        return strategyBO;
    }

    /**
     * 查询指定策略下所有的抽奖规则
     */
    @Override
    public List<RafflePoolBO> findByRuleTypeAndStrategyOrAwardIdOrderByRuleGradeAsc(Long strategyId) {
        List<RafflePool> rafflePoolList = raffleRuleRepository.findByStrategyIdOrderByRuleGradeAsc(
                strategyId,
                Sort.by("ruleGrade").ascending()
        );

        return BeanUtil.copyToList(rafflePoolList, RafflePoolBO.class);
    }

    /**
     * 将数据库中指定策略的奖品查询出来装配到 redis
     **/
    @Override
    public List<AwardBO> findAwardsByStrategyId(Long strategyId) {
        RList<AwardBO> rList = redissonClient.getList("strategy_" + strategyId + "_awards");
        if (!rList.isEmpty()) return rList;

        List<AwardBO> awardBOS = BeanUtil.copyToList(
                awardRepository.findByStrategyId(strategyId), AwardBO.class
        );
        // 将查询结果存入Redis缓存
        rList.addAll(awardBOS);

        return awardBOS;
    }

/*    @Override
    public List<AwardBO> queryRuleLockAwards(Long strategyId) {
        RList<AwardBO> rList = redissonClient.getList("strategy_" + strategyId + "_awards_Lock");
        if (!rList.isEmpty()) return rList;

        // 缓存中没有则查询数据库
        List<AwardBO> awardRuleLockBOS = queryCommonAwards(strategyId).stream()
//                .filter(AwardBO -> !AwardBO.getRules().contains("rule_lock"))
                .toList();

        rList.addAll(awardRuleLockBOS);

        return awardRuleLockBOS;
    }*/

    @Override
    public List<AwardBO> queryRuleLockLongAwards(Long strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_LockLong";
        RList<AwardBO> rList = redissonClient.getList(cacheKey);
        if (!rList.isEmpty()) return rList;

        List<AwardBO> awardRuleLockBOS = findAwardsByStrategyId(strategyId).stream()
//                .filter(AwardBO -> !AwardBO.getRules().contains("rule_lock_long"))
                .toList();

        rList.addAll(awardRuleLockBOS);

        return awardRuleLockBOS;
    }

    @Override
    public AwardBO queryWorstAwardId(Long strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_Blacklist";
        // 由于黑名单奖品只有一个，所以不用rList
        RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
        if (bucket.isExists()) return (AwardBO) bucket.get();

        List<AwardBO> awardBOs = findAwardsByStrategyId(strategyId);
        Optional<AwardBO> optional = awardBOs.stream()
//                .filter(AwardBO -> AwardBO.getRules().contains("rule_common_blacklist"))
                .findFirst();

        // 存入redis
        bucket.set(optional.orElse(null));

        return optional.orElse(null);
    }

    @Override
    public List<AwardBO> queryRuleGrandAwards(Long strategyId) {
        RList<AwardBO> rlist = redissonClient.getList("strategy_" + strategyId + "_awards_Grand");
        if (!rlist.isEmpty()) return rlist;

        List<AwardBO> awardBOs = findAwardsByStrategyId(strategyId);
        List<AwardBO> awardRuleGrandBOS = awardBOs.stream()
//                .filter(AwardBO -> AwardBO.getRules().contains("rule_lock"))
                .toList();

        rlist.addAll(awardRuleGrandBOS);

        return awardRuleGrandBOS;
    }

    @Override
    public void assembleAwardsCount(Long strategyId) {
        List<AwardBO> awardBOS = findAwardsByStrategyId(strategyId);
        for (AwardBO awardBO : awardBOS) {
            String cacheKey = "strategy_" + strategyId + "_awards_" + awardBO.getAwardId() + "_count";
            RAtomicLong rAtomicLong = redissonClient.getAtomicLong(cacheKey);

            if (!rAtomicLong.isExists()) {
                rAtomicLong.set(awardBO.getAwardCount());
            }
        }
    }

    /**
     * 装配权重对象
     **/
    // 将权重对象插入到Redis中
    @Override
    public void insertWeightRandom(Long strategyId, String awardRule, WeightRandom<Long> wr) {
        String cacheKey = strategyId + "_" + awardRule + "weightRandom_";
        redissonClient.getBucket(cacheKey).set(wr);
    }

    // 更新权重对象
    @Override
    public void updateWeightRandom(Long strategyId, String awardRule, WeightRandom<Long> wr) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_" + awardRule;
        RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
        if (bucket.isExists()) {
            bucket.set(wr);
        }
    }

    // 根据策略ID，从redis中查询权重对象
    @Override
    public WeightRandom<Long> queryRuleCommonWeightRandom(Long strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_Common";
        return (WeightRandom<Long>) redissonClient.getBucket(cacheKey).get();
    }

    // 根据策略ID，从redis中查询除去锁定的权重对象
    @Override
    public WeightRandom<Long> queryRuleLockWeightRandom(Long strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_Lock";
        return (WeightRandom<Long>) redissonClient.getBucket(cacheKey).get();
    }

    @Override
    public WeightRandom<Long> queryRuleLockLongWeightRandom(Long strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_LockLong";
        return (WeightRandom<Long>) redissonClient.getBucket(cacheKey).get();
    }

    @Override
    public WeightRandom<Long> queryRuleGrandAwardIdByRandom(Long strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_Grand";
        return (WeightRandom<Long>) redissonClient.getBucket(cacheKey).get();
    }

}
