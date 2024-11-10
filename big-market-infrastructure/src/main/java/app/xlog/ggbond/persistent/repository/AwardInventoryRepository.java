package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RList;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class AwardInventoryRepository implements IAwardInventoryRepository {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private IRaffleRepository raffleRepository;
    @Resource
    private AwardRepository awardRepository;

    /**
     * 更新奖品库存
     */
    @Override
    public Boolean decreaseAwardCount(Long strategyId, Long awardId) {
        String cacheKey = "strategy_" + strategyId + "_awards_" + awardId + "_count";
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(cacheKey);

        if (rAtomicLong.isExists()) {
            // 返回扣减完成之后的值
            long surplus = rAtomicLong.decrementAndGet();
            if (surplus >= 0) {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减成功，剩余库存：{}", awardId, surplus);
                return true;
            } else {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减失败", awardId);
                return false;
            }
        }

        // 一般来说装配好了奖品库存，不会走到这里
        log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减失败", awardId);
        return false;
    }

    /**
     * 将该奖品从缓存中的所有抽奖池里移除
     */
    @Override
    public void removeAwardFromPools(Long strategyId, Long awardId) {
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
                    List<WeightRandom.WeightObj<Long>> weightObjs = raffleRepository.queryCommonAwards(strategyId).stream()
                            .filter(AwardBO -> !Objects.equals(AwardBO.getAwardId(), awardId))
                            .map(AwardBO -> new WeightRandom.WeightObj<>(
                                    AwardBO.getAwardId(),
                                    AwardBO.getAwardRate()
                            ))
                            .toList();
                    WeightRandom<Long> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("抽奖领域 - 在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
                case 2 -> {
                    List<WeightRandom.WeightObj<Long>> weightObjs = raffleRepository.queryRuleLockAwards(strategyId).stream()
                            .filter(AwardBO -> Objects.equals(AwardBO.getAwardId(), awardId))
                            .map(AwardBO -> new WeightRandom.WeightObj<>(
                                    AwardBO.getAwardId(),
                                    AwardBO.getAwardRate()
                            ))
                            .toList();
                    WeightRandom<Long> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("抽奖领域 - 在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
                case 3 -> {
                    List<WeightRandom.WeightObj<Long>> weightObjs = raffleRepository.queryRuleLockLongAwards(strategyId).stream()
                            .filter(AwardBO -> Objects.equals(AwardBO.getAwardId(), awardId))
                            .map(AwardBO -> new WeightRandom.WeightObj<>(
                                    AwardBO.getAwardId(),
                                    AwardBO.getAwardRate()
                            ))
                            .toList();
                    WeightRandom<Long> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("抽奖领域 - 在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
                case 4 -> {
                    List<WeightRandom.WeightObj<Long>> weightObjs = raffleRepository.queryRuleGrandAwards(strategyId).stream()
                            .filter(awardBO -> Objects.equals(awardBO.getAwardId(), awardId))
                            .map(AwardBO -> new WeightRandom.WeightObj<>(
                                    AwardBO.getAwardId(),
                                    AwardBO.getAwardRate()
                            ))
                            .toList();
                    WeightRandom<Long> wr = RandomUtil.weightRandom(weightObjs);
                    redissonClient.getBucket(cacheKey.getValue()).set(wr);

                    log.atInfo().log("抽奖领域 - 在缓存中的 {} 抽奖池，移除奖品 {} 成功", cacheKey.getValue(), awardId);
                }
            }
        }
    }

    @Override
    public void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO) {
        RQueue<DecrQueueVO> rQueue = redissonClient.getQueue("awards_DecrQueue");
        rQueue.add(decrQueueVO);
    }

    /**
     * 查询出队列中的一个扣减信息
     */
    @Override
    public DecrQueueVO queryDecrAwardCountFromQueue() {
        RQueue<DecrQueueVO> rQueue = redissonClient.getQueue("awards_DecrQueue");
        return rQueue.poll();
    }

    /**
     * 根据策略id，奖品id，更新数据库中对应奖品的库存
     *
     * @param decrQueueVO 队列中的一个扣减信息
     */
    @Override
    public void updateAwardCount(DecrQueueVO decrQueueVO) {
        awardRepository.decrementAwardCountByStrategyIdAndAwardId(
                decrQueueVO.getStrategyId(),
                decrQueueVO.getAwardId()
        );
    }
}