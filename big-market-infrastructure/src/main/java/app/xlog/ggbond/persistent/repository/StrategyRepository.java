package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.mapper.AwardMapper;
import app.xlog.ggbond.persistent.po.Award;
import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.WeightRandom;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
策略仓库实现类
 */
@Repository
public class StrategyRepository implements IStrategyRepository {
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AwardMapper awardMapper;

    @Override
    public List<AwardBO> queryAwards(int strategyId, String rule) {
        String cacheKey = "strategy_" + strategyId + "_awards_" + rule;

        // Redis缓存中存在则直接返回
        RList<AwardBO> rList = redissonClient.getList(cacheKey);
        if (!rList.isEmpty() && rList != null) {
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
    public List<AwardBO> queryRuleLockAwards(int strategyId, String rule) {
        // 先从缓存中取
        String cacheKey = "strategy_" + strategyId + "_awards_" + rule;
        RList<AwardBO> rList = redissonClient.getList(cacheKey);
        if (!rList.isEmpty() && rList != null) {
            return rList;
        }

        // 缓存中没有则查询数据库
        List<AwardBO> awardBOs = queryAwards(strategyId, "Common");
        // 过滤
        List<AwardBO> awardRuleLockBOS = awardBOs.stream()
                .limit(awardBOs.size() - 4)
                .toList();

        // 存入redis
        rList.addAll(awardRuleLockBOS);

        return awardRuleLockBOS;
    }

    // 将权重对象插入到Redis中
    @Override
    public void insertWeightRandom(int strategyId, WeightRandom<Integer> wr, String awardRule) {
        String cacheKey = "strategy_" + strategyId + "_awards_WeightRandom_" + awardRule;
        redissonClient.getBucket(cacheKey).set(wr);
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
}
