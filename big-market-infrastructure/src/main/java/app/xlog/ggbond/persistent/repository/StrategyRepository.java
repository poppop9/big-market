package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.mapper.AwardMapper;
import app.xlog.ggbond.persistent.mapper.StrategyMapper;
import app.xlog.ggbond.persistent.po.Award;
import app.xlog.ggbond.persistent.po.Strategy;
import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.model.StrategyBO;
import app.xlog.ggbond.strategy.model.vo.DecrQueueVO;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/*
策略仓库实现类
 */
@Repository
public class StrategyRepository implements IStrategyRepository {
    private static final Logger log = LoggerFactory.getLogger(StrategyRepository.class);
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private StrategyMapper strategyMapper;
    @Autowired
    private AwardMapper awardMapper;

    /**
     * 装配策略
     **/
    @Override
    public StrategyBO queryStrategys(Integer strategyId) {
        String cacheKey = "strategy_" + strategyId;

        RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
        if (bucket.isExists()) {
            return (StrategyBO) bucket.get();
        }

        QueryWrapper<Strategy> queryWrapper = new QueryWrapper<Strategy>()
                .eq("strategy_id", strategyId);
        Strategy strategy = strategyMapper.selectOne(queryWrapper);

        StrategyBO strategyBO = BeanUtil.copyProperties(strategy, StrategyBO.class);
        bucket.set(strategyBO);

        return strategyBO;
    }

    /**
     * 将数据库中的数据查询出来装配到 redis
     **/
    @Override
    public List<AwardBO> queryCommonAwards(int strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_Common";

        // Redis缓存中存在则直接返回
        RList<AwardBO> rList = redissonClient.getList(cacheKey);
        if (!rList.isEmpty()) {
            return rList;
        }

        // Redis缓存中不存在则查询数据库
        QueryWrapper<Award> queryWrapper = new QueryWrapper<Award>()
                .eq("strategy_id", strategyId);

        List<Award> awardPOs = awardMapper.selectList(queryWrapper);
        // 将PO转换为BO
        List<AwardBO> awardBOS = BeanUtil.copyToList(awardPOs, AwardBO.class);

        // 将查询结果存入Redis缓存
        rList.addAll(awardBOS);

        return awardBOS;
    }

    @Override
    public List<AwardBO> queryRuleLockAwards(int strategyId) {
        // 先从缓存中取
        String cacheKey = "strategy_" + strategyId + "_awards_Lock";
        RList<AwardBO> rList = redissonClient.getList(cacheKey);
        if (!rList.isEmpty() && rList != null) {
            return rList;
        }

        // 缓存中没有则查询数据库
        List<AwardBO> awardBOs = queryCommonAwards(strategyId);
        // 过滤
        List<AwardBO> awardRuleLockBOS = awardBOs.stream()
                .filter(
                        AwardBO -> !AwardBO.getRules().contains("rule_lock")
                )
                .toList();

        // 存入redis
        rList.addAll(awardRuleLockBOS);

        return awardRuleLockBOS;
    }

    @Override
    public List<AwardBO> queryRuleLockLongAwards(int strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_LockLong";
        RList<AwardBO> rList = redissonClient.getList(cacheKey);
        if (!rList.isEmpty() && rList != null) {
            return rList;
        }

        List<AwardBO> awardBOs = queryCommonAwards(strategyId);
        List<AwardBO> awardRuleLockBOS = awardBOs.stream()
                .filter(
                        AwardBO -> !AwardBO.getRules().contains("rule_lock_long")
                )
                .toList();

        rList.addAll(awardRuleLockBOS);

        return awardRuleLockBOS;
    }

    @Override
    public AwardBO queryWorstAwardId(Integer strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_Blacklist";
        // 由于黑名单奖品只有一个，所以不用rList
        RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
        if (bucket.isExists()) {
            return (AwardBO) bucket.get();
        }

        List<AwardBO> awardBOs = queryCommonAwards(strategyId);
        AwardBO awardBO = awardBOs.stream()
                .filter(
                        AwardBO -> AwardBO.getRules().contains("rule_common_blacklist")
                )
                .findFirst()
                .get();

        // 存入redis
        bucket.set(awardBO);

        return awardBO;
    }

    @Override
    public List<AwardBO> queryRuleGrandAwards(Integer strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_Grand";
        RList<AwardBO> rlist = redissonClient.getList(cacheKey);
        if (!rlist.isEmpty() && rlist != null) {
            return rlist;
        }

        List<AwardBO> awardBOs = queryCommonAwards(strategyId);
        List<AwardBO> awardRuleGrandBOS = awardBOs.stream()
                .filter(
                        AwardBO -> AwardBO.getRules().contains("rule_lock")
                )
                .toList();

        rlist.addAll(awardRuleGrandBOS);

        return awardRuleGrandBOS;
    }

    @Override
    public void assembleAwardsCount(Integer strategyId) {
        List<AwardBO> awardBOS = queryCommonAwards(strategyId);
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
    public void insertWeightRandom(int strategyId, String awardRule, WeightRandom<Integer> wr) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_" + awardRule;
        redissonClient.getBucket(cacheKey).set(wr);
    }

    // 更新权重对象
    @Override
    public void updateWeightRandom(int strategyId, String awardRule, WeightRandom<Integer> wr) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_" + awardRule;
        RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
        if (bucket.isExists()) {
            bucket.set(wr);
        }
    }

    // 根据策略ID，从redis中查询权重对象
    @Override
    public WeightRandom<Integer> queryRuleCommonWeightRandom(int strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_Common";
        return (WeightRandom<Integer>) redissonClient.getBucket(cacheKey).get();
    }

    // 根据策略ID，从redis中查询除去锁定的权重对象
    @Override
    public WeightRandom<Integer> queryRuleLockWeightRandom(int strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_Lock";
        return (WeightRandom<Integer>) redissonClient.getBucket(cacheKey).get();
    }

    @Override
    public WeightRandom<Integer> queryRuleLockLongWeightRandom(Integer strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_LockLong";
        return (WeightRandom<Integer>) redissonClient.getBucket(cacheKey).get();
    }

    @Override
    public WeightRandom<Integer> queryRuleGrandAwardIdByRandom(Integer strategyId) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_Grand";
        return (WeightRandom<Integer>) redissonClient.getBucket(cacheKey).get();
    }

    @Override
    public Boolean decreaseAwardCount(Integer strategyId, Integer awardId) {
        String cacheKey = "strategy_" + strategyId + "_awards_" + awardId + "_count";
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(cacheKey);

        if (rAtomicLong.isExists()) {
            // 返回扣减完成之后的值
            long surplus = rAtomicLong.decrementAndGet();
            if (surplus >= 0) {
                log.atInfo().log("奖品 {} 库存扣减成功，剩余库存：{}", awardId, surplus);
                return true;
            } else {
                log.atInfo().log("奖品 {} 库存扣减失败", awardId);
                return false;
            }
        }

        // 一般来说装配好了奖品库存，不会走到这里
        log.atInfo().log("奖品 {} 库存扣减失败", awardId);
        return false;
    }

    @Override
    public void removeAwardFromPools(Integer strategyId, Integer awardId) {
        // 由于黑名单抽奖池在redis中只有一个对象，所以不用去除，黑名单用户就让他一直抽随机积分就好了，而且随机积分的库存绝对够
        String[] cacheKeyLists = {
                "strategy_" + strategyId + "_awards_Common",
                "strategy_" + strategyId + "_awards_Lock",
                "strategy_" + strategyId + "_awards_LockLong",
                "strategy_" + strategyId + "_awards_Grand"
        };
        // 获取redis中的奖品列表，过滤掉要移除的奖品
        for (String cacheKey : cacheKeyLists) {
            RList<AwardBO> rList = redissonClient.getList(cacheKey);

            Optional<AwardBO> optionalAwardBO = rList.stream()
                    .filter(AwardBO -> Objects.equals(AwardBO.getAwardId(), awardId))
                    .findFirst();
            optionalAwardBO.ifPresent(rList::remove);
        }

        // 使用map集合和switch，动态判断要执行的方法
        Set<Map.Entry<Integer, String>> cacheKeyWeightRandoms = Map.of(
                1, "strategy_" + strategyId + "_awards_WeightRandom_Common",
                2, "strategy_" + strategyId + "_awards_WeightRandom_Lock",
                3, "strategy_" + strategyId + "_awards_WeightRandom_LockLong",
                4, "strategy_" + strategyId + "_awards_WeightRandom_Grand"
        ).entrySet();

        for (Map.Entry<Integer, String> cacheKey : cacheKeyWeightRandoms) {
            switch (cacheKey.getKey()) {
                case 1 -> {
                    List<WeightRandom.WeightObj<Integer>> weightObjs = queryCommonAwards(strategyId).stream()
                            .filter(AwardBO -> !Objects.equals(AwardBO.getAwardId(), awardId))
                            .map(
                                    AwardBO -> new WeightRandom.WeightObj<>(
                                            AwardBO.getAwardId(),
                                            AwardBO.getAwardRate()
                                    )
                            ).toList();
                    WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
                case 2 -> {
                    List<WeightRandom.WeightObj<Integer>> weightObjs = queryRuleLockAwards(strategyId).stream()
                            .filter(AwardBO -> Objects.equals(AwardBO.getAwardId(), awardId))
                            .map(
                                    AwardBO -> new WeightRandom.WeightObj<>(
                                            AwardBO.getAwardId(),
                                            AwardBO.getAwardRate()
                                    )
                            ).toList();
                    WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
                case 3 -> {
                    List<WeightRandom.WeightObj<Integer>> weightObjs = queryRuleLockLongAwards(strategyId).stream()
                            .filter(AwardBO -> Objects.equals(AwardBO.getAwardId(), awardId))
                            .map(
                                    AwardBO -> new WeightRandom.WeightObj<>(
                                            AwardBO.getAwardId(),
                                            AwardBO.getAwardRate()
                                    )
                            ).toList();
                    WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
                case 4 -> {
                    List<WeightRandom.WeightObj<Integer>> weightObjs = queryRuleGrandAwards(strategyId).stream()
                            .filter(awardBO -> Objects.equals(awardBO.getAwardId(), awardId))
                            .map(
                                    AwardBO -> new WeightRandom.WeightObj<>(
                                            AwardBO.getAwardId(),
                                            AwardBO.getAwardRate()
                                    )
                            ).toList();
                    WeightRandom<Integer> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
            }
        }
    }

    @Override
    public void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO) {
        // 建立队列
        RQueue<Object> rQueue = redissonClient.getQueue("awards_DecrQueue");
        // 写入队列
        rQueue.add(decrQueueVO);
    }

    @Override
    public DecrQueueVO queryDecrAwardCountFromQueue() {
        RQueue<Object> rQueue = redissonClient.getQueue("awards_DecrQueue");
        return (DecrQueueVO) rQueue.poll();
    }

    @Override
    public void updateAwardCount(DecrQueueVO decrQueueVO) {
        UpdateWrapper<Award> updateWrapper = new UpdateWrapper<Award>()
                .setSql("award_count = award_count - 1")
                .eq("strategy_id", decrQueueVO.getStrategyId())
                .eq("award_id", decrQueueVO.getAwardId());

        awardMapper.update(null, updateWrapper);
    }
}
