package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.persistent.repository.jpa.RafflePoolRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 策略仓库实现类
 */
@Repository
public class RaffleRepository implements IRaffleRepository {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private AwardRepository awardRepository;
    @Resource
    private RafflePoolRepository rafflePoolRepository;

    /**
     * 根据策略id，查询对应的所有奖品
     **/
    @Override
    public List<AwardBO> findAwardsByStrategyId(Long strategyId) {
        return BeanUtil.copyToList(
                awardRepository.findByStrategyId(strategyId), AwardBO.class
        );
    }

    /**
     * 根据奖品Id，查询对应的奖品
     */
    @Override
    public AwardBO findAwardByAwardId(Long awardId) {
        Award award = awardRepository.findByAwardId(awardId);
        return BeanUtil.copyProperties(award, AwardBO.class);
    }

    /**
     * 装配所有奖品的库存
     */
    @Override
    public void assembleAllAwardCountBystrategyId(Long strategyId) {
        findAwardsByStrategyId(strategyId).forEach(item -> {
            String cacheKey = strategyId + "_" + item.getAwardId() + "_Count";
            RAtomicLong rAtomicLong = redissonClient.getAtomicLong(cacheKey);

            if (!rAtomicLong.isExists()) {
                rAtomicLong.set(item.getAwardCount());
            }
        });
    }

    /**
     * 根据策略Id，查询对应的所有抽奖池规则
     */
    @Override
    public List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId) {
        List<RafflePool> allRafflePool = rafflePoolRepository.findByStrategyId(strategyId);
        return BeanUtil.copyToList(allRafflePool, RafflePoolBO.class);
    }

    /**
     * 装配权重对象
     **/
    // 将权重对象插入到Redis中
    @Override
    public void insertWeightRandom(Long strategyId, String dispatchParam, WeightRandom<Long> wr) {
        String cacheKey = strategyId + "_" + dispatchParam + "_WeightRandom";
        redissonClient.getBucket(cacheKey).set(wr);
    }

    /**
     * 从 redis 中查询出指定的权重对象
     */
    @Override
    public WeightRandom<Long> findWeightRandom(Long strategyId, String dispatchParam) {
        String cacheKey = strategyId + "_" + dispatchParam + "_WeightRandom";
        return (WeightRandom<Long>) redissonClient.getBucket(cacheKey).get();
    }

}
