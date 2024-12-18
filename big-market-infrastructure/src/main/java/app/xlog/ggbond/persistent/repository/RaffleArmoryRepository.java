package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import app.xlog.ggbond.persistent.po.raffle.UserRaffleConfig;
import app.xlog.ggbond.persistent.po.raffle.UserRaffleHistory;
import app.xlog.ggbond.persistent.repository.jpa.*;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleConfigBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleHistoryBO;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
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
    private UserRaffleHistoryRepository userRaffleHistoryRepository;
    @Resource
    private UserRaffleConfigRepository userRaffleConfigRepository;
    @Resource
    private ActivityRepository activityRepository;

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
     * 查询 - 根据用户id，策略id，查询用户的抽奖历史
     */
    @Override
    public List<UserRaffleHistoryBO> getWinningAwardsInfo(Long userId, Long strategyId) {
        List<UserRaffleHistory> list = userRaffleHistoryRepository.findByUserIdAndStrategyIdOrderByCreateTimeAsc(userId, strategyId);
        return BeanUtil.copyToList(list, UserRaffleHistoryBO.class);
    }

    /**
     * 查询 - 根据活动id，用户id，查询用户的所有奖品
     */
    @Override
    public List<AwardBO> findAllAwards(Long activityId, Long userId) {
        Long strategyId = userRaffleConfigRepository.findByUserIdAndActivityId(activityId, userId).getStrategyId();
        return BeanUtil.copyToList(
                awardRepository.findByStrategyId(strategyId), AwardBO.class
        );
    }

    /**
     * 查询 - 跟据活动id，用户id，查询用户的策略id
     */
    @Override
    public Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId) {
        UserRaffleConfig userConfig = userRaffleConfigRepository.findByUserIdAndActivityId(userId, activityId);
        return Optional.ofNullable(userConfig.getStrategyId())
                .orElseGet(() -> activityRepository.findByActivityId(activityId).getDefaultStrategyId());
    }

    /**
     * 查询 - 查询当前用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId, Long strategyId) {
        UserRaffleConfig userRaffleConfig = userRaffleConfigRepository.findByUserIdAndStrategyId(userId, strategyId);
        return BeanUtil.copyProperties(userRaffleConfig, UserRaffleConfigBO.class).getRaffleTime();
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
