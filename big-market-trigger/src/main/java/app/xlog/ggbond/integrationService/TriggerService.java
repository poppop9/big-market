package app.xlog.ggbond.integrationService;

import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.service.IActivityService;
import app.xlog.ggbond.activity.service.statusFlow.AOEventCenter;
import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.StrategyBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.raffle.service.IRaffleDispatch;
import app.xlog.ggbond.recommend.RecommendService;
import app.xlog.ggbond.resp.BigMarketRespCode;
import app.xlog.ggbond.reward.model.RewardTaskBO;
import app.xlog.ggbond.reward.service.IRewardService;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 组合应用服务
 */
@Slf4j
@Service
public class TriggerService implements Serializable {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ThreadPoolTaskScheduler myScheduledThreadPool;

    @Resource
    private transient ISecurityService securityService;
    @Resource
    private transient IActivityService activityService;
    @Resource
    private transient IRaffleArmory raffleArmory;
    @Resource
    private transient IRaffleDispatch raffleDispatch;
    @Resource
    private transient IRewardService rewardService;
    @Resource
    private transient RecommendService recommendService;
    @Resource
    private transient AOEventCenter aoEventCenter;

    /**
     * 抽奖领域 - 根据活动id和当前用户，抽取一个奖品id
     */
    @Transactional
    public AwardBO raffle(Long activityId) {
        // 1. 获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        Long userId = user.getUserId();

        // 2. 跟据活动id，用户id，查询用户的策略id
        Long strategyId = raffleArmory.findStrategyIdByActivityIdAndUserId(activityId, userId);

        // 3. 发布事件，消费活动单
        aoEventCenter.publishEffectiveToUsedEvent(AOContext.builder()
                .activityId(activityId)
                .userId(userId)
                .build()
        );

        // 4. 抽奖
        RaffleFilterContext context = raffleDispatch.raffle(RaffleFilterContext.builder()
                .strategyId(strategyId)
                .userBO(app.xlog.ggbond.raffle.model.bo.UserBO.builder()
                        .userId(userId)
                        .isBlacklistUser(securityService.isBlacklistUser(userId))
                        .build()
                )
                .saSession(StpUtil.getSession())
                .build()
        );
        log.atInfo().log("抽奖领域 - " + userId + " 抽到 {} 活动的 {} 奖品", activityId, context.getAwardId());

        // 5. 写入发奖的task表
        long rewardId = rewardService.insertRewardTask(RewardTaskBO.builder()
                .userId(userId)
                .userRaffleHistoryId(context.getUserRaffleHistoryId())
                .isIssued(false)
                .build()
        );

        // 6. 发送发奖的mq消息
        rewardService.sendRewardToMQ(RewardTaskBO.builder()
                .rewardId(rewardId)
                .userId(userId)
                .userRaffleHistoryId(context.getUserRaffleHistoryId())
                .build()
        );

        // 7. 查询奖品详情
        return raffleArmory.findAwardByStrategyIdAndAwardId(context.getStrategyId(), context.getAwardId())
                .setAwardRate(null)
                .setAwardCount(null)
                .setAwardSort(null)
                .setCurrentRaffleCount(context.getUserBO().getRaffleTime() + 1);
    }

    /**
     * 安全领域 - 登录
     */
    @SneakyThrows
    public UserBO doLogin(Long userId, String password) {
        boolean isSuccess = securityService.doLogin(userId, password);
        // 1. 判断是否登录成功
        if (!isSuccess) {
            throw new BigMarketException(BigMarketRespCode.WRONG_USERNAME_OR_PASSWORD);
        }

        // 2. 把activityId塞到session里，后面的操作都可以直接从这里取
        long activityId = Long.parseLong(SaHolder.getRequest().getParam("activityId"));
        StpUtil.getSession().set("activityId", activityId);

        // 3. 将该用户的角色信息放入session
        securityService.insertPermissionIntoSession();

        // todo 这个地方也有并发问题要修复
        if (!securityService.acquireLoginLock(userId)) {
            throw new BigMarketException(BigMarketRespCode.FREQUENT_LOGIN);
        } else {
            CompletableFuture<Boolean> doLoginCompletableFuture = CompletableFuture.supplyAsync(() -> {
                // 4. 检查该用户是否有策略，如果没有则ai生成推荐商品
                if (!securityService.existsByUserIdAndActivityId(activityId, userId)) {
                    List<AwardBO> noAwardIdAwardBOS;
                    // 4.1 判断用户是否有购买历史
                    if (securityService.existsUserPurchaseHistory(userId)) {
                        // 4.1.1 查询用户购买历史，生成推荐奖品
                        List<UserPurchaseHistoryBO> userPurchaseHistoryBOList = securityService.findUserPurchaseHistory(userId);
                        noAwardIdAwardBOS = recommendService.recommendAwardByUserPurchaseHistory(
                                "你是一个推荐系统，根据用户的购买历史推荐最能吸引该用户的商品。",
                                userPurchaseHistoryBOList
                        );
                    } else {
                        // 4.1.2 无购买历史，从海量用户的购买历史中生成热销产品
                        List<UserPurchaseHistoryBO> recentPurchaseHistoryList = securityService.findRecentPurchaseHistory();
                        noAwardIdAwardBOS = recommendService.recommendHotSaleProductByRecentPurchaseHistory(
                                "你是一个推荐系统，根据海量用户的购买历史，推算出近期用户喜好，给某个用户推荐商品。",
                                recentPurchaseHistoryList
                        );
                    }

                    // 4.2 插入数据库
                    StrategyBO strategyBO = raffleArmory.insertAwardList(userId, activityId, noAwardIdAwardBOS);
                    raffleArmory.insertUserRaffleConfig(userId, activityId, strategyBO.getStrategyId());
                }

                // 5. 如果该用户没有活动账户，则初始化一个活动账户
                activityService.initActivityAccount(userId, activityId);

                // 6. 装配
                Long strategyId = raffleArmory.findStrategyIdByActivityIdAndUserId(activityId, userId);
                raffleArmory.assembleRaffleWeightRandomByStrategyId2(strategyId);  // 装配该策略所需的所有权重对象Map
                raffleArmory.assembleAllAwardCountByStrategyId(strategyId);  // 装配该策略所需的所有奖品的库存Map
                raffleArmory.assembleAwardList(strategyId);  // 装配该策略所需的所有奖品列表

                return true;
            }, myScheduledThreadPool);
            StpUtil.getSession().set("doLoginCompletableFuture", doLoginCompletableFuture);
            securityService.releaseLoginLock(userId);
        }

        return UserBO.builder()
                .userId(userId)
                .userName(securityService.findUserByUserId(userId).getUserName())
                .token(StpUtil.getTokenInfo().getTokenValue())
                .build();
    }

    /**
     * 活动领域 - 充值活动单
     */
    public AOContext rechargeAO(AOContext aoContext) {
        // 初始状态 --->>> 待支付状态
        aoContext = aoEventCenter.publishInitialToPendingPaymentEvent(
                aoContext.setUserId(securityService.getLoginIdDefaultNull())
        );

        // 待支付状态 --->>> 有效状态
        aoContext = aoEventCenter.publishPendingPaymentToEffectiveEvent(aoContext);

        return aoContext;
    }

    /**
     * 活动领域 - 查询所有待支付的活动单
     */
    public List<ActivityOrderBO> findAllPendingPaymentAO(Long activityId) {
        Long userId = securityService.getLoginIdDefaultNull();
        return activityService.findAllPendingPaymentAO(activityId, userId);
    }

}
