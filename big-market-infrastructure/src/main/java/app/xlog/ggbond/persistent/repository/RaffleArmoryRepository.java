package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.persistent.po.raffle.*;
import app.xlog.ggbond.persistent.repository.jpa.*;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleConfigBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleHistoryBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.WeightRandom;
import jakarta.annotation.Resource;
import org.redisson.api.*;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;
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
    private RafflePoolJpa rafflePoolJpa;
    @Resource
    private StrategyAwardJpa strategyAwardJpa;
    @Resource
    private AwardJpa awardJpa;
    @Resource
    private UserRaffleConfigJpa userRaffleConfigJpa;
    @Resource
    private UserRaffleHistoryJpa userRaffleHistoryJpa;
    @Resource
    private StrategyJpa strategyJpa;

    /**
     * 查询 - 权重对象 - 从 redis 中查询出指定的权重对象
     */
    @Override
    public WeightRandom<Long> findWeightRandom(Long strategyId, String dispatchParam) {
        return (WeightRandom<Long>) redissonClient.getBucket(GlobalConstant.RedisKey.getWeightRandomCacheKey(strategyId, dispatchParam)).get();
    }

    /**
     * 查询 - 权重对象 - 从 redis 中查询出指定的权重对象
     */
    @Override
    public WeightRandom<Long> findWeightRandom2(Long strategyId, String dispatchParam) {
        RMap<String, WeightRandom<Long>> rMap = redissonClient.getMap(GlobalConstant.RedisKey.getWeightRandomMapCacheKey(strategyId));
        return rMap.get(dispatchParam);
    }

    /**
     * 查询 - 根据策略id，查询对应的所有奖品
     **/
    @Override
    public List<AwardBO> findAwardsByStrategyId(Long strategyId) {
        List<StrategyAward> strategyAwardList = strategyAwardJpa.findByStrategyIdOrderByAwardSortAsc(strategyId);
        return strategyAwardList.stream()
                .map(item -> {
                    AwardBO awardBO = BeanUtil.copyProperties(
                            awardJpa.findByAwardId(item.getAwardId()), AwardBO.class
                    );
                    awardBO.setAwardIdStr(item.getAwardId().toString());
                    awardBO.setAwardCount(item.getAwardCount());
                    awardBO.setAwardSort(item.getAwardSort());
                    awardBO.setAwardRate(item.getAwardRate());
                    return awardBO;
                })
                .toList();
    }

    /**
     * 查询 - 根据奖品Id，查询对应的奖品
     */
    @Override
    public AwardBO findAwardByAwardId(Long awardId) {
        Award award = awardJpa.findByAwardId(awardId);
        AwardBO awardBO = BeanUtil.copyProperties(award, AwardBO.class);

        return awardBO.setAwardIdStr(award.getAwardId().toString());
    }

    /**
     * 查询 - 根据策略id，奖品Id，查询奖品详情
     */
    @Override
    public AwardBO findAwardByStrategyIdAndAwardId(Long strategyId, Long awardId) {
        Award award = awardJpa.findByAwardId(awardId);
        AwardBO awardBO = BeanUtil.copyProperties(award, AwardBO.class);
        StrategyAward strategyAward = strategyAwardJpa.findByStrategyIdAndAwardId(strategyId, awardId);

        return awardBO.setAwardRate(strategyAward.getAwardRate())
                .setAwardCount(strategyAward.getAwardCount())
                .setAwardSort(strategyAward.getAwardSort())
                .setAwardIdStr(award.getAwardId().toString());
    }

    /**
     * 查询 - 根据策略Id，查询对应的所有抽奖池规则
     */
    @Override
    public List<RafflePoolBO> findAllRafflePoolByStrategyId(Long strategyId) {
        List<RafflePool> allRafflePool = rafflePoolJpa.findByStrategyId(strategyId);
        return BeanUtil.copyToList(allRafflePool, RafflePoolBO.class);
    }

    /**
     * 查询 - 根据活动id，用户id，查询用户的所有奖品
     */
    @Override
    public List<AwardBO> findAllAwards(Long activityId, Long userId) {
        Long strategyId = userRaffleConfigJpa.findByUserIdAndActivityId(userId, activityId).getStrategyId();

        // 存在缓存，直接返回
        RList<AwardBO> rList = redissonClient.getList(GlobalConstant.RedisKey.getAwardListCacheKey(strategyId));
        if (rList.isExists()) return rList;

        // 不存在，重新从数据库中查询
        return findAwardsByStrategyId(strategyId);
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

        RMap<Long, Long> rMap = redissonClient.getMap(GlobalConstant.RedisKey.getAwardCountMapCacheKey(strategyId));

        // 由于redis中的数据实时性比数据库高，所有如果存在不覆盖，而是更新过期时间
        if (!rMap.isExists()) {
            rMap.putAll(collect);
        }
        rMap.expire(Duration.ofSeconds(GlobalConstant.RedisKey.REDIS_EXPIRE_TIME));
    }

    /**
     * 装配 - 权重对象 - 将权重对象Map插入到Redis中
     */
    @Override
    public void insertWeightRandom(Long strategyId, Map<String, WeightRandom<Long>> wrMap) {
        RMap<String, WeightRandom<Long>> rMap = redissonClient.getMap(GlobalConstant.RedisKey.getWeightRandomMapCacheKey(strategyId));

        // 由于redis中的数据实时性比数据库高，所有如果存在不覆盖，而是更新过期时间
        if (!rMap.isExists()) {
            rMap.putAll(wrMap);
        }
        rMap.expire(Duration.ofSeconds(GlobalConstant.RedisKey.REDIS_EXPIRE_TIME));
    }

    /**
     * 装配 - 权重对象 - 将权重对象Map强制插入到Redis中
     */
    @Override
    public void forceInsertWeightRandom(Long strategyId, Map<String, WeightRandom<Long>> wrMap) {
        RMap<String, WeightRandom<Long>> rMap = redissonClient.getMap(GlobalConstant.RedisKey.getWeightRandomMapCacheKey(strategyId));
        rMap.putAll(wrMap);
    }

    /**
     * 装配 - 装配奖品列表
     */
    @Override
    public void assembleAwardList(Long strategyId) {
        RList<AwardBO> rList = redissonClient.getList(GlobalConstant.RedisKey.getAwardListCacheKey(strategyId));
        // 由于redis中的数据实时性比数据库高，所有如果存在不覆盖，而是更新过期时间
        if (rList.isExists()) {
            rList.expire(Duration.ofSeconds(GlobalConstant.RedisKey.REDIS_EXPIRE_TIME));
            return;
        }

        List<StrategyAward> strategyAwardList = strategyAwardJpa.findByStrategyIdOrderByAwardSortAsc(strategyId);
        List<Long> awardIdList = strategyAwardList.stream().map(StrategyAward::getAwardId).toList();

        // 获取所有奖品详情
        Map<Long, Award> awardIdAwardMap = awardJpa.findByAwardIdIn(awardIdList).stream()
                .collect(Collectors.toMap(
                        Award::getAwardId,
                        item -> item
                ));

        // 转为 AwardBO
        List<AwardBO> awardBOList = strategyAwardList.stream()
                .map(item -> AwardBO.builder()
                        .awardId(item.getAwardId())
                        .awardIdStr(item.getAwardId().toString())
                        .awardTitle(awardIdAwardMap.get(item.getAwardId()).getAwardTitle())
                        .awardSubtitle(awardIdAwardMap.get(item.getAwardId()).getAwardSubtitle())
                        .awardRate(item.getAwardRate())
                        .awardCount(item.getAwardCount())
                        .awardSort(item.getAwardSort())
                        .build()
                )
                .toList();

        // 写入redis
        rList.addAll(awardBOList);
        rList.expire(Duration.ofSeconds(GlobalConstant.RedisKey.REDIS_EXPIRE_TIME));
    }

    /**
     * 插入 - 插入奖品
     */
    @Override
    public List<AwardBO> insertAwardList(List<AwardBO> awardBOS) {
        List<Award> awards = awardJpa.saveAll(BeanUtil.copyToList(awardBOS, Award.class));
        return BeanUtil.copyToList(awards, AwardBO.class);
    }

    /**
     * 插入 - 插入策略
     */
    @Override
    public long insertStrategy(long activityId) {
        Strategy strategy = strategyJpa.save(Strategy.builder().activityId(activityId).build());
        return strategy.getStrategyId();
    }

    /**
     * 插入 - 插入策略奖品
     */
    @Override
    public void insertStrategyAwardList(long strategyId, List<AwardBO> awardBOS) {
        List<StrategyAward> list = awardBOS.stream()
                .map(item -> StrategyAward.builder()
                        .strategyId(strategyId)
                        .awardId(item.getAwardId())
                        .awardCount(item.getAwardCount())
                        .awardRate(item.getAwardRate())
                        .awardSort(item.getAwardSort())
                        .build()
                )
                .toList();
        strategyAwardJpa.saveAll(list);
    }

    /**
     * 插入 - 插入抽奖池
     */
    @Override
    public void insertRafflePoolList(long strategyId, List<AwardBO> awardBOS) {
        List<Map.Entry<Double, ArrayList<AwardBO>>> rateItemListMap = awardBOS.stream().collect(Collectors.toMap(
                        AwardBO::getAwardRate,
                        item -> new ArrayList<>(List.of(item)),  // 使用可变集合
                        (o, n) -> {
                            o.addAll(n);
                            return o;
                        }
                ))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getKey))  // 从小到大排序
                .toList();

        // 生成五个抽奖池（所有奖品，没有1级别的奖品，没有1级别没有2级别，1级别2级别奖品，只有最低级奖品）
        List<Long> allAwardPool = rateItemListMap.stream().flatMap(item -> item.getValue().stream()).map(AwardBO::getAwardId).toList();
        rafflePoolJpa.save(RafflePool.builder()
                .strategyId(strategyId).awardIds(allAwardPool)
                .rafflePoolType(RafflePool.RafflePoolType.NormalTime).rafflePoolName("AllAwardPool")
                .normalTimeStartValue(20L).normalTimeEndValue(Long.MAX_VALUE)
                .build()
        );

        List<Long> no1stAwardPool = rateItemListMap.stream().skip(1).flatMap(item -> item.getValue().stream()).map(AwardBO::getAwardId).toList();
        rafflePoolJpa.save(RafflePool.builder()
                .strategyId(strategyId).awardIds(no1stAwardPool)
                .rafflePoolType(RafflePool.RafflePoolType.NormalTime).rafflePoolName("No1stAwardPool")
                .normalTimeStartValue(10L).normalTimeEndValue(19L)
                .build()
        );

        List<Long> no1stAnd2ndAwardPool = rateItemListMap.stream().skip(2).flatMap(item -> item.getValue().stream()).map(AwardBO::getAwardId).toList();
        rafflePoolJpa.save(RafflePool.builder()
                .strategyId(strategyId).awardIds(no1stAnd2ndAwardPool)
                .rafflePoolType(RafflePool.RafflePoolType.NormalTime).rafflePoolName("No1stAnd2ndAwardPool")
                .normalTimeStartValue(0L).normalTimeEndValue(9L)
                .build()
        );

        List<Long> istAnd2ndAwardPool = rateItemListMap.stream().limit(2).flatMap(item -> item.getValue().stream()).map(AwardBO::getAwardId).toList();
        rafflePoolJpa.save(RafflePool.builder()
                .strategyId(strategyId).awardIds(istAnd2ndAwardPool)
                .rafflePoolType(RafflePool.RafflePoolType.SpecialTime).rafflePoolName("IstAnd2ndAwardPool")
                .specialTimeValue(50L)
                .build()
        );

        List<Long> blacklistPool = rateItemListMap.stream().skip(3).flatMap(item -> item.getValue().stream()).map(AwardBO::getAwardId).toList();
        rafflePoolJpa.save(RafflePool.builder()
                .strategyId(strategyId).awardIds(blacklistPool)
                .rafflePoolType(RafflePool.RafflePoolType.SpecialRule).rafflePoolName("BlacklistPool")
                .build()
        );
    }

    /**
     * 判断 - 用户是否在抽奖中
     */
    @Override
    public boolean isUserInRaffle(Long userId) {
        RBitSet rBitSet = redissonClient.getBitSet(GlobalConstant.RedisKey.USER_IN_RAFFLE_BIT_SET);
        return rBitSet.get(userId);
    }

    /**
     * 修改 - 在BitSet中给用户加锁
     */
    @Override
    public void lockUserInBitSet(Long userId) {
        RBitSet rBitSet = redissonClient.getBitSet(GlobalConstant.RedisKey.USER_IN_RAFFLE_BIT_SET);
        rBitSet.set(userId);
    }

    /**
     * 修改 - 在BitSet中给用户解锁
     */
    @Override
    public void unLockUserInBitSet(Long userId) {
        RBitSet rBitSet = redissonClient.getBitSet(GlobalConstant.RedisKey.USER_IN_RAFFLE_BIT_SET);
        rBitSet.clear(userId);
    }

    /**
     * 查询 - 跟据活动id，用户id，查询用户的策略id
     */
    @Override
    public Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId) {
        UserRaffleConfig userConfig = userRaffleConfigJpa.findByUserIdAndActivityId(userId, activityId);
        return userConfig == null ? null : userConfig.getStrategyId();
    }

    /**
     * 查询 - 根据用户id，策略id，查询用户的抽奖历史
     */
    @Override
    public List<UserRaffleHistoryBO> getWinningAwardsInfo(Long userId, Long strategyId) {
        List<UserRaffleHistory> list = userRaffleHistoryJpa.findByUserIdAndStrategyIdOrderByCreateTimeAsc(userId, strategyId);
        return BeanUtil.copyToList(list, UserRaffleHistoryBO.class);
    }

    /**
     * 查询 - 查询当前用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId, Long strategyId) {
        UserRaffleConfig userRaffleConfig = userRaffleConfigJpa.findByUserIdAndStrategyId(userId, strategyId);
        return BeanUtil.copyProperties(userRaffleConfig, UserRaffleConfigBO.class).getRaffleTime();
    }

    /**
     * 插入 - 插入用户抽奖配置
     */
    @Override
    public void insertUserRaffleConfig(Long userId, long activityId, Long strategyId) {
        userRaffleConfigJpa.save(UserRaffleConfig.builder()
                .userId(userId)
                .activityId(activityId)
                .strategyId(strategyId)
                .build()
        );
    }

    /**
     * 查询 - 查询用户的抽奖次数
     */
    @Override
    public Long findRaffleCount(Long activityId, Long userId) {
        UserRaffleConfig userRaffleConfig = userRaffleConfigJpa.findByUserIdAndActivityId(userId, activityId);
        return userRaffleConfig.getRaffleTime();
    }

}
