package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.persistent.repository.jpa.RafflePoolRepository;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleConfigRepository;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.WeightRandom;
import jakarta.annotation.Resource;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抽奖领域 - 抽奖的兵工厂仓库
 * <p>
 * - 1.初始化装配各种数据
 * - 2.作为外部查询抽奖信息的接口
 */
@Repository
public class RaffleArmoryRepository implements IRaffleArmoryRepo {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RafflePoolRepository rafflePoolRepository;
    @Resource
    private AwardRepository awardRepository;
    @Resource
    private UserRaffleConfigRepository userRaffleConfigRepository;

    /**
     * 查询 - 权重对象 - 从 redis 中查询出指定的权重对象
     */
    @Override
    public WeightRandom<Long> findWeightRandom(Long strategyId, String dispatchParam) {
        return (WeightRandom<Long>) redissonClient.getBucket(GlobalConstant.getWeightRandomCacheKey(strategyId, dispatchParam)).get();
    }

    /**
     * 查询 - 权重对象 - 从 redis 中查询出指定的权重对象
     */
    @Override
    public WeightRandom<Long> findWeightRandom2(Long strategyId, String dispatchParam) {
        RMap<String, WeightRandom<Long>> rMap = redissonClient.getMap(GlobalConstant.getWeightRandomMapCacheKey(strategyId));
        return rMap.get(dispatchParam);
    }

    /**
     * 查询 - 权重对象 - 从 redis 中查询出指定策略的所有权重对象
     */
    @Override
    public List<WeightRandom<Long>> findAllWeightRandomByStrategyId(Long strategyId) {
        return Arrays.stream(RaffleFilterContext.DispatchParam.values())
                .map(item -> (WeightRandom<Long>) redissonClient.getBucket(GlobalConstant.getWeightRandomCacheKey(strategyId, item.name())).get())
                .toList();
    }

    /**
     * 查询 - 根据策略id，查询对应的所有奖品
     **/
    @Override
    public List<AwardBO> findAwardsByStrategyId(Long strategyId) {
        return BeanUtil.copyToList(
                awardRepository.findByStrategyId(strategyId), AwardBO.class
        );
    }

    /**
     * 查询 - 根据奖品Id，查询对应的奖品
     */
    @Override
    public AwardBO findAwardByAwardId(Long awardId) {
        Award award = awardRepository.findByAwardId(awardId);
        return BeanUtil.copyProperties(award, AwardBO.class);
    }

    /**
     * 查询 - 根据策略Id，查询对应的所有抽奖池规则
     */
    @Override
    public List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId) {
        List<RafflePool> allRafflePool = rafflePoolRepository.findByStrategyId(strategyId);
        return BeanUtil.copyToList(allRafflePool, RafflePoolBO.class);
    }

    /**
     * 查询 - 根据活动id，用户id，查询用户的所有奖品
     */
    @Override
    public List<AwardBO> findAllAwards(Long activityId, Long userId) {
        Long strategyId = userRaffleConfigRepository.findByUserIdAndActivityId(userId, activityId).getStrategyId();
        return BeanUtil.copyToList(
                awardRepository.findByStrategyId(strategyId), AwardBO.class
        );
    }

    /**
     * 装配 - 装配所有奖品的库存
     */
    @Override
    public void assembleAllAwardCountByStrategyId(Long strategyId) {
        Map<Long, Long> collect = findAwardsByStrategyId(strategyId).stream().collect(Collectors.toMap(
                AwardBO::getAwardId,
                AwardBO::getAwardCount
        ));

        RMap<Object, Object> rMap = redissonClient.getMap(GlobalConstant.getAwardCountMapCacheKey(strategyId));
        if (rMap.isExists()) rMap.clear();

        rMap.putAll(collect);
        rMap.expire(Duration.ofSeconds(GlobalConstant.redisExpireTime));

/*        findAwardsByStrategyId(strategyId).forEach(item -> {
            RAtomicLong rAtomicLong = redissonClient.getAtomicLong(GlobalConstant.getAwardCountCacheKey(strategyId, item.getAwardId()));

            if (!rAtomicLong.isExists()) {
                rAtomicLong.set(item.getAwardCount());
                rAtomicLong.expire(Duration.ofSeconds(GlobalConstant.redisExpireTime));
            }
        });*/
    }

    /**
     * 装配 - 将权重对象插入到Redis中
     */
    @Override
    public void insertWeightRandom(Long strategyId, String dispatchParam, WeightRandom<Long> wr) {
        RBucket<Object> bucket = redissonClient.getBucket(GlobalConstant.getWeightRandomCacheKey(strategyId, dispatchParam));
        bucket.set(wr);

        bucket.expire(Duration.ofSeconds(GlobalConstant.redisExpireTime));
    }

    /**
     * 装配 - 权重对象 - 将权重对象Map插入到Redis中
     */
    @Override
    public void insertWeightRandom(Long strategyId, Map<String, WeightRandom<Long>> wrMap) {
        RMap<String, WeightRandom<Long>> rMap = redissonClient.getMap(GlobalConstant.getWeightRandomMapCacheKey(strategyId));
        if (rMap.isExists()) {
            rMap.clear();
        }
        rMap.putAll(wrMap);

        rMap.expire(Duration.ofSeconds(GlobalConstant.redisExpireTime));
    }

}
