package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
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
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(strategyId + "_" + awardId + "_Count");
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
     * 将该奖品从缓存中的所有抽奖池权重对象中移除
     */
    @Override
    public void removeAwardFromPools(Long strategyId, Long awardId) {
        // 所有的权重对象集合
        raffleRepository.findAllRafflePoolByStrategyId(strategyId).stream()
                .peek(item -> item.getAwardIds().remove(awardId))
                .forEach(item -> {
                    // 生成权重集合
                    List<WeightRandom.WeightObj<Long>> weightObjs = item.getAwardIds().stream()
                            .map(child -> {
                                AwardBO award = raffleRepository.findAwardByAwardId(child);
                                return new WeightRandom.WeightObj<>(child, award.getAwardRate());
                            })
                            .toList();
                    WeightRandom<Long> WeightRandom = RandomUtil.weightRandom(weightObjs);

                    // 将WeightRandom对象存入redis，方便后续抽奖调用
                    raffleRepository.insertWeightRandom(strategyId, item.getRafflePoolName(), WeightRandom);
                });
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