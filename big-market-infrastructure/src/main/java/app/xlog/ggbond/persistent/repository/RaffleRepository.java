package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.mapper.AwardMapper;
import app.xlog.ggbond.persistent.mapper.StrategyMapper;
import app.xlog.ggbond.persistent.po.Award;
import app.xlog.ggbond.persistent.po.Strategy;
import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.model.StrategyBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
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
public class RaffleRepository implements IRaffleRepository {
    private static final Logger log = LoggerFactory.getLogger(RaffleRepository.class);
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
        Optional<AwardBO> optional = awardBOs.stream()
                .filter(
                        AwardBO -> AwardBO.getRules().contains("rule_common_blacklist")
                )
                .findFirst();

        // 存入redis
        bucket.set(optional.orElse(null));

        return optional.orElse(null);
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

}
