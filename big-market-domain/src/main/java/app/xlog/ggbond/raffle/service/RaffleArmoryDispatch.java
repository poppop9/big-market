package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.StrategyBO;
import app.xlog.ggbond.raffle.model.bo.UserBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import app.xlog.ggbond.raffle.service.filterChain.RaffleFilterChain;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
     * 装配 - 根据指定策略id，装配该策略所需的所有权重对象
     */
    @Override
    @Deprecated
    public void assembleRaffleWeightRandomByStrategyId(Long strategyId) {
        // 所有的权重对象集合
        raffleArmoryRepo.findAllRafflePoolByStrategyId(strategyId)
                .forEach(item -> {
                    // 生成权重集合
                    List<WeightRandom.WeightObj<Long>> weightObjs = item.getAwardIds().stream()
                            .map(child -> {
                                AwardBO award = raffleArmoryRepo.findAwardByAwardId(child);
                                return new WeightRandom.WeightObj<>(child, award.getAwardRate());
                            })
                            .toList();
                    WeightRandom<Long> weightRandom = RandomUtil.weightRandom(weightObjs);

                    // 将WeightRandom对象存入redis，方便后续抽奖调用
                    raffleArmoryRepo.insertWeightRandom(strategyId, item.getRafflePoolName(), weightRandom);
                });
    }

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
        // 插入奖品表
        awardBOS = awardBOS.stream().peek(item -> item.setAwardId(IdUtil.getSnowflakeNextId())).collect(Collectors.toList());
        raffleArmoryRepo.insertAwardList(awardBOS);

        // 插入策略表
        long strategyId = raffleArmoryRepo.insertStrategy(activityId);

        // 插入策略奖品表
        awardBOS.add(AwardBO.randomPointsAward);
        raffleArmoryRepo.insertStrategyAwardList(strategyId, awardBOS);

        // 插入抽奖池
        raffleArmoryRepo.insertRafflePoolList(strategyId, awardBOS);

        return StrategyBO.builder().strategyId(strategyId).build();
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
