package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.persistent.po.security.UserRaffleHistory;
import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleConfigRepository;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleHistoryRepository;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抽奖领域 - 抽奖调度仓库
 */
@Slf4j
@Repository
public class RaffleDispatchRepository implements IRaffleDispatchRepo {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AwardRepository awardRepository;
    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;
    @Resource
    private UserRaffleHistoryRepository userRaffleHistoryRepository;
    @Resource
    private UserRaffleConfigRepository userRaffleConfigRepository;

    /**
     * 抽奖池 - 将该奖品从缓存中的所有抽奖池权重对象中移除
     */
    @Override
    public void removeAwardFromPools(Long strategyId, Long awardId) {
        Map<String, WeightRandom<Long>> collect = raffleArmoryRepo.findAllRafflePoolByStrategyId(strategyId).stream()
                .peek(item -> item.getAwardIds().remove(awardId))
                .collect(Collectors.toMap(
                        RafflePoolBO::getRafflePoolName,
                        item -> {
                            // 生成权重集合
                            List<WeightRandom.WeightObj<Long>> weightObjs = item.getAwardIds().stream()
                                    .map(child -> {
                                        AwardBO award = raffleArmoryRepo.findAwardByAwardId(child);
                                        return new WeightRandom.WeightObj<>(child, award.getAwardRate());
                                    })
                                    .toList();
                            return RandomUtil.weightRandom(weightObjs);
                        }
                ));

        // 将WeightRandom对象存入redis，方便后续抽奖调用
        raffleArmoryRepo.insertWeightRandom(strategyId, collect);
    }

    /**
     * 库存 - 根据策略id，奖品id，更新数据库中对应奖品的库存
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

    /**
     * 库存 - 更新奖品库存
     */
    @Override
    public Boolean decreaseAwardCount(Long strategyId, Long awardId) {
        RMap<Long, Long> rMap = redissonClient.getMap(GlobalConstant.getAwardCountMapCacheKey(strategyId));
        if (rMap.isExists()) {
            Long surplus = rMap.compute(awardId, (k, v) -> v != null ? v - 1 : 0);
            if (surplus > 0) {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减成功，剩余库存：{}", awardId, surplus);
                return true;
            } else if (surplus == 0) {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减完成，剩余库存：{}", awardId, surplus);
                // 将该奖品从缓存中的抽奖池里移除
                removeAwardFromPools(strategyId, awardId);
                return true;
            }
        }

        // 一般来说装配好了奖品库存，不会走到这里
        log.atWarn().log("抽奖领域 - 奖品 {} 库存扣减失败", awardId);
        return false;
    }

    /**
     * 库存 - 将扣减信息写入队列
     */
    @Override
    public void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO) {
        RQueue<DecrQueueVO> rQueue = redissonClient.getQueue(GlobalConstant.getAwardCountDecrQueue());
        rQueue.add(decrQueueVO);
    }

    /**
     * 库存 - 查询出队列中的一个扣减信息
     */
    @Override
    public DecrQueueVO queryDecrAwardCountFromQueue() {
        RQueue<DecrQueueVO> rQueue = redissonClient.getQueue(GlobalConstant.getAwardCountDecrQueue());
        return rQueue.poll();
    }

    /**
     * 库存 - 更新所有奖品库存的过期时间
     */
    @Override
    public void updateAllAwardCountExpireTime(Long strategyId) {
        raffleArmoryRepo.findAwardsByStrategyId(strategyId).forEach(item -> {
            RAtomicLong rAtomicLong = redissonClient.getAtomicLong(GlobalConstant.getAwardCountCacheKey(strategyId, item.getAwardId()));

            if (rAtomicLong.isExists()) {
                rAtomicLong.expire(Duration.ofSeconds(GlobalConstant.redisExpireTime));
            }
        });
    }

    /**
     * 抽奖次数 - 给用户的指定策略增加抽奖次数
     */
    @Override
    public void addUserRaffleTimeByStrategyId(Long userId, Long strategyId) {
        userRaffleConfigRepository.updateRaffleTimeByUserIdAndStrategyId(
                userId,
                strategyId,
                userRaffleConfigRepository.findByUserIdAndStrategyId(userId, strategyId).getRaffleTime() + 1
        );
    }

    /**
     * 用户抽奖历史 - 添加用户抽奖流水记录
     */
    @Override
    public void addUserRaffleFlowRecordFilter(Long userId, Long strategyId, Long awardId) {
        userRaffleHistoryRepository.save(UserRaffleHistory.builder()
                .userId(userId)
                .strategyId(strategyId)
                .awardId(awardId)
                .build()
        );
    }

    /**
     * 权重对象 - 更新所有权重对象的过期时间
     */
    @Override
    public void updateAllWeightRandomExpireTime(Long strategyId) {
        Arrays.stream(RaffleFilterContext.DispatchParam.values())
                .map(item -> redissonClient.getBucket(GlobalConstant.getWeightRandomCacheKey(strategyId, item.name())))
                .forEach(item -> item.expire(Duration.ofSeconds(GlobalConstant.redisExpireTime)));
    }

    /**
     * 权重对象 - 更新所有权重对象Map的过期时间
     */
    @Override
    public void updateAllWeightRandomExpireTime2(Long strategyId) {
        redissonClient.getMap(GlobalConstant.getWeightRandomMapCacheKey(strategyId))
                .expire(Duration.ofSeconds(GlobalConstant.redisExpireTime));
    }

}
