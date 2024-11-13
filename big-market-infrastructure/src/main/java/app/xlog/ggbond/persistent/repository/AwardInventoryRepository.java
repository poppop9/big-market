package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RList;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
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
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong("strategy_" + strategyId + "_awards_" + awardId + "_count");
        if (rAtomicLong.isExists()) {
            long surplus = rAtomicLong.decrementAndGet();  // 返回扣减完成之后的值
            if (surplus > 0) {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减成功，剩余库存：{}", awardId, surplus);
                return true;
            } else if (surplus == 0) {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减完成，剩余库存：{}", awardId, surplus);
                // 将该奖品从缓存中的所有抽奖池里移除
                removeAwardFromPools(strategyId, awardId);
                return true;
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
        // 1. 在redis的所有抽奖池中，移除指定奖品。由于黑名单抽奖池在redis中只有一个对象，所以不用去除，黑名单用户就让他一直抽随机积分就好了，而且随机积分的库存绝对够
        String[] cacheKeyLists = {
                "strategy_" + strategyId + "_awards_Common",
                "strategy_" + strategyId + "_awards_Lock",
                "strategy_" + strategyId + "_awards_LockLong",
                "strategy_" + strategyId + "_awards_Grand"
        };
        for (String cacheKey : cacheKeyLists) {
            RList<AwardBO> rList = redissonClient.getList(cacheKey);
            rList.stream()
                    .filter(AwardBO -> Objects.equals(AwardBO.getAwardId(), awardId))
                    .findFirst()
                    .ifPresent(rList::remove);
        }

        // 2. 调整权重对象
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