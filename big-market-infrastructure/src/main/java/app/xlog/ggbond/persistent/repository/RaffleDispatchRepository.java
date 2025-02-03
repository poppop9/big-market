package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.mq.MQEventCenter;
import app.xlog.ggbond.persistent.po.raffle.UserRaffleHistory;
import app.xlog.ggbond.persistent.repository.jpa.AwardJpa;
import app.xlog.ggbond.persistent.repository.jpa.StrategyAwardJpa;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleConfigJpa;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleHistoryJpa;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Arrays;
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
    private MQEventCenter mqEventCenter;

    @Resource
    private AwardJpa awardJpa;
    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;
    @Resource
    private UserRaffleHistoryJpa userRaffleHistoryJpa;
    @Resource
    private UserRaffleConfigJpa userRaffleConfigJpa;
    @Autowired
    private StrategyAwardJpa strategyAwardJpa;

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
        strategyAwardJpa.decrementAwardCountByStrategyIdAndAwardId(
                decrQueueVO.getStrategyId(),
                decrQueueVO.getAwardId()
        );
    }

    /**
     * 库存 - 更新奖品库存
     */
    @Override
    @SneakyThrows
    public Boolean decreaseAwardCount(Long strategyId, Long awardId) {
        RMap<Long, Long> rMap = redissonClient.getMap(GlobalConstant.RedisKey.getAwardCountMapCacheKey(strategyId));
        if (rMap.isExists()) {
            Long surplus = rMap.compute(awardId, (k, v) -> (v != null && v > 0) ? (v - 1) : 0);
            if (surplus > 0) {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减成功，剩余库存：{}", awardId, surplus);
                return true;
            } else if (surplus == 0) {
                log.atInfo().log("抽奖领域 - 奖品 {} 库存扣减完成，剩余库存：{}", awardId, surplus);
                // 将该奖品从缓存中的抽奖池里移除
                removeAwardFromPools(strategyId, awardId);
                return true;
            } else {
                // 这里如果并发高，会出现扣减为负数的情况，但是没关系，返回false，后续会重新调度
                log.atError().log("抽奖领域 - 奖品 {} 库存扣减失败", awardId);
                removeAwardFromPools(strategyId, awardId);  // 将该奖品从缓存中的抽奖池里移除
                return false;
            }
        }

        log.atError().log("抽奖领域 - 奖品 {} 库存扣减失败，可能库存没有装配成功", awardId);
        return false;
    }

    /**
     * 库存 - 将扣减信息写入队列
     */
    @Override
    public void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO) {
        RQueue<DecrQueueVO> rQueue = redissonClient.getQueue(GlobalConstant.RedisKey.AWARD_COUNT_DECR_QUEUE);
        rQueue.add(decrQueueVO);
    }

    /**
     * 库存 - 将扣减信息写入kafka
     */
    @Override
    public void addDecrAwardCountToMQ(DecrQueueVO decrQueueVO) {
        boolean isSuccess = mqEventCenter.sendMessage(
                GlobalConstant.KafkaConstant.DECR_AWARD_INVENTORY,
                MQMessage.<DecrQueueVO>builder()
                        .data(decrQueueVO)
                        .build()
        );

        if (isSuccess) {
            log.debug("抽奖领域 - 将扣减信息写入kafka - 消息发送成功：{}", decrQueueVO);
        } else {
            log.error("抽奖领域 - 将扣减信息写入kafka - 消息发送失败：{}", decrQueueVO);
        }
    }

    /**
     * 库存 - 查询出队列中的一个扣减信息
     */
    @Override
    public DecrQueueVO queryDecrAwardCountFromQueue() {
        RQueue<DecrQueueVO> rQueue = redissonClient.getQueue(GlobalConstant.RedisKey.AWARD_COUNT_DECR_QUEUE);
        return rQueue.poll();
    }

    /**
     * 库存 - 更新所有奖品库存的过期时间
     */
    @Override
    public void updateAllAwardCountExpireTime(Long strategyId) {
        raffleArmoryRepo.findAwardsByStrategyId(strategyId).forEach(item -> {
            RAtomicLong rAtomicLong = redissonClient.getAtomicLong(GlobalConstant.RedisKey.getAwardCountCacheKey(strategyId, item.getAwardId()));

            if (rAtomicLong.isExists()) {
                rAtomicLong.expire(Duration.ofSeconds(GlobalConstant.RedisKey.REDIS_EXPIRE_TIME));
            }
        });
    }

    /**
     * 抽奖次数 - 给用户的指定策略增加抽奖次数
     */
    @Override
    public void addUserRaffleTimeByStrategyId(Long userId, Long strategyId) {
        userRaffleConfigJpa.updateRaffleTimeByUserIdAndStrategyId(
                userId,
                strategyId
        );
    }

    /**
     * 用户抽奖历史 - 添加用户抽奖流水记录
     */
    @Override
    public Long addUserRaffleFlowRecordFilter(Long userId, Long strategyId, Long awardId) {
        UserRaffleHistory userRaffleHistory = userRaffleHistoryJpa.save(UserRaffleHistory.builder()
                .userId(userId)
                .strategyId(strategyId)
                .awardId(awardId)
                .build()
        );
        return userRaffleHistory.getId();
    }

    /**
     * 权重对象 - 更新所有权重对象的过期时间
     */
    @Override
    public void updateAllWeightRandomExpireTime(Long strategyId) {
        Arrays.stream(RaffleFilterContext.DispatchParam.values())
                .map(item -> redissonClient.getBucket(GlobalConstant.RedisKey.getWeightRandomCacheKey(strategyId, item.name())))
                .forEach(item -> item.expire(Duration.ofSeconds(GlobalConstant.RedisKey.REDIS_EXPIRE_TIME)));
    }

    /**
     * 权重对象 - 更新所有权重对象Map的过期时间
     */
    @Override
    public void updateAllWeightRandomExpireTime2(Long strategyId) {
        redissonClient.getMap(GlobalConstant.RedisKey.getWeightRandomMapCacheKey(strategyId))
                .expire(Duration.ofSeconds(GlobalConstant.RedisKey.REDIS_EXPIRE_TIME));
    }

}
