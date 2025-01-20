package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.bo.*;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.raffle.service.filterChain.RaffleFilterChain;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 抽奖领域 - 及（装配，查询，调度）于一身的实现类
 */
@Slf4j
@Service
public class RaffleArmoryDispatch implements IRaffleArmory, IRaffleDispatch {

    @Resource
    private RaffleFilterChain raffleFilterChain;
    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;

    /**
     * 装配 - 根据指定策略id，装配该策略所需的所有权重对象Map
     */
    @Override
    public void assembleRaffleWeightRandomByStrategyId2(Long strategyId) {
        // 所有的权重对象集合
        Map<String, WeightRandom<Long>> collect = raffleArmoryRepo.findAllRafflePoolByStrategyId(strategyId).stream()
                .collect(Collectors.toMap(
                        RafflePoolBO::getRafflePoolName,
                        item -> {
                            List<AwardBO> awardsByStrategyId = raffleArmoryRepo.findAwardsByStrategyId(item.getStrategyId());
                            // 生成权重集合
                            List<WeightRandom.WeightObj<Long>> weightObjs = awardsByStrategyId.stream()
                                    .map(child -> new WeightRandom.WeightObj<>(child.getAwardId(), child.getAwardRate()))
                                    .toList();
                            return RandomUtil.weightRandom(weightObjs);
                        }
                ));

        // 将WeightRandom对象存入redis，方便后续抽奖调用
        raffleArmoryRepo.insertWeightRandom(strategyId, collect);
    }

    /**
     * 装配 - 装配所有奖品的库存Map
     */
    @Override
    public void assembleAllAwardCountByStrategyId(Long strategyId) {
        raffleArmoryRepo.assembleAllAwardCountByStrategyId(strategyId);
    }

    /**
     * 根据活动id，用户id，查询用户的所有奖品
     */
    @Override
    public List<AwardBO> findAllAwards(Long activityId, Long userId) {
        return raffleArmoryRepo.findAllAwards(activityId, userId);
    }

    /**
     * 插入 - 将该用户的所有奖品信息插入到数据库
     */
    @Override
    @Transactional
    public StrategyBO insertAwardList(Long userId, long activityId, List<AwardBO> awardBOS) {
        // 1. 插入策略表
        long strategyId = raffleArmoryRepo.insertStrategy(activityId);

        // 2. 插入奖品表
        int i = new Random().nextInt(4);
        awardBOS = awardBOS.stream()
                .peek(item -> {
                    long snowflakeNextId;
                    do {
                        snowflakeNextId = IdUtil.getSnowflakeNextId();
                    } while (snowflakeNextId % 4 != i);
                    item.setAwardId(snowflakeNextId);
                })
                .collect(Collectors.toList());
        raffleArmoryRepo.insertAwardList(awardBOS);

        // 3. 插入策略奖品表
        awardBOS.add(AwardBO.randomPointsAward);
        raffleArmoryRepo.insertStrategyAwardList(strategyId, awardBOS);

        // 4. 插入抽奖池
        raffleArmoryRepo.insertRafflePoolList(strategyId, awardBOS);

        return StrategyBO.builder().strategyId(strategyId).build();
    }

    /**
     * 查询 - 查询用户某个活动的中奖奖品信息
     */
    @Override
    public List<UserRaffleHistoryBO> findWinningAwardsInfo(Long activityId, Long userId) {
        // 跟据活动id，用户id，查询用户的策略id
        Long strategyId = raffleArmoryRepo.findStrategyIdByActivityIdAndUserId(activityId, userId);
        return raffleArmoryRepo.getWinningAwardsInfo(userId, strategyId);
    }

    /**
     * 查询 - 根据活动id，用户id，查询用户的策略id
     */
    @Override
    public Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId) {
        return raffleArmoryRepo.findStrategyIdByActivityIdAndUserId(activityId, userId);
    }

    /**
     * 查询 - 查询用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId, Long strategyId) {
        return raffleArmoryRepo.queryRaffleTimesByUserId(userId, strategyId);
    }

    /**
     * 插入 - 插入用户抽奖配置
     */
    @Override
    public void insertUserRaffleConfig(Long userId, long activityId, Long strategyId) {
        raffleArmoryRepo.insertUserRaffleConfig(userId, activityId, strategyId);
    }

    /**
     * 调度 - 根据策略ID，指定的调度参数，获取对应抽奖池中的随机奖品
     */
    @Override
    public Long findAwardIdByDispatchParam(Long strategyId, RaffleFilterContext.DispatchParam dispatchParam) {
        WeightRandom<Long> weightRandom = raffleArmoryRepo.findWeightRandom2(strategyId, dispatchParam.toString());
        return weightRandom.next();
    }

    /**
     * 调度 - 根据策略id，抽取奖品
     */
    @Override
    public Long getAwardId(Long strategyId, UserBO userBO) {
        // 执行过滤器链
        return raffleFilterChain.executeFilterChain(RaffleFilterContext.builder()
                .userBO(userBO)
                .strategyId(strategyId)
                .middleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS)
                .build()
        );
    }

}
