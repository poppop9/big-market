package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import app.xlog.ggbond.persistent.po.security.UserRaffleConfig;
import app.xlog.ggbond.persistent.po.security.UserRaffleHistory;
import app.xlog.ggbond.persistent.repository.jpa.*;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.WeightRandom;
import jakarta.annotation.Resource;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 抽奖领域 - 抽奖的兵工厂仓库:
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
     * 装配 - 将权重对象插入到Redis中
     */
    @Override
    public void insertWeightRandom(Long strategyId, String dispatchParam, WeightRandom<Long> wr) {
        String cacheKey = strategyId + "_" + dispatchParam + "_WeightRandom";
        redissonClient.getBucket(cacheKey).set(wr);
    }

}
