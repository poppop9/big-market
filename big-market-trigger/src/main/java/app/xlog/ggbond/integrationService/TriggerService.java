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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 组合应用服务
 */
@Slf4j
@Service
public class TriggerService implements Serializable {

    @Resource
    private TransactionTemplate transactionTemplate;

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
    public AwardBO raffle(Long activityId) {
        // 1. 获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        Long userId = user.getUserId();

        // 3. 加锁
        raffleDispatch.acquireRaffleLock(userId);
        RaffleFilterContext context = transactionTemplate.execute(status -> {
            try {
                // 4. 发布事件，消费活动单
                // todo 临时注释
                    /*aoEventCenter.publishEffectiveToUsedEvent(AOContext.builder()
                            .activityId(activityId)
                            .userId(userId)
                            .build()
                    );*/

                // 5. 抽奖
                RaffleFilterContext contextTemp = raffleDispatch.raffle(RaffleFilterContext.builder()
                        .activityId(activityId)
                        .userBO(app.xlog.ggbond.raffle.model.bo.UserBO.builder()
                                .userId(userId)
                                .isBlacklistUser(securityService.isBlacklistUser(userId))
                                .build())
                        .saSession(StpUtil.getSession())
                        .build()
                );
                log.atInfo().log("抽奖领域 - " + userId + " 抽到 {} 活动的 {} 奖品", activityId, contextTemp.getAwardId());
                return contextTemp;
            } catch (BigMarketException e) {
                throw e;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });

        // 7. 写入发奖的task表
        long rewardId = rewardService.insertRewardTask(RewardTaskBO.builder()
                .userId(userId)
                .userRaffleHistoryId(context.getUserRaffleHistoryId())
                .isIssued(false)
                .build()
        );

        // 8. 发送发奖的mq消息
        rewardService.sendRewardToMQ(RewardTaskBO.builder()
                .rewardId(rewardId)
                .userId(userId)
                .userRaffleHistoryId(context.getUserRaffleHistoryId())
                .build()
        );

        // 9. 查询奖品详情
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
        if (!securityService.acquireLoginLock(userId)) {
            throw new BigMarketException(BigMarketRespCode.FREQUENT_LOGIN);
        } else {
            // 1. 判断是否登录成功
            if (!securityService.doLogin(userId, password)) {
                throw new BigMarketException(BigMarketRespCode.WRONG_USERNAME_OR_PASSWORD);
            }

            long activityId;
            try {
                // 2. 把activityId塞到session里，后面的操作都可以直接从这里取
                activityId = Long.parseLong(SaHolder.getRequest().getParam("activityId"));
                StpUtil.getSession().set("activityId", activityId);
                // 3. 将该用户的角色信息放入session
                securityService.insertPermissionIntoSession();
            } catch (RuntimeException e) {
                // 这段代码有任何报错，都要注销登录用户
                securityService.logoutByToken(StpUtil.getTokenValue());
                throw e;
            }

            if (securityService.existsByUserIdAndActivityId(activityId, userId)) {
                // 4. 用户存在策略，直接装配
                Long strategyId = raffleArmory.findStrategyIdByActivityIdAndUserId(activityId, userId);
                raffleArmory.assembleRaffleWeightRandomByStrategyId2(strategyId);  // 装配该策略所需的所有权重对象Map
                raffleArmory.assembleAllAwardCountByStrategyId(strategyId);  // 装配该策略所需的所有奖品的库存Map
                raffleArmory.assembleAwardList(strategyId);  // 装配该策略所需的所有奖品列表
                StpUtil.getSession().set("isNewUser", false);
            } else {
                // 5. 用户不存在策略，异步生成并装配
                CompletableFuture<Boolean> doLoginCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    List<AwardBO> noAwardIdAwardBOS;
                    // 5.1 判断用户是否有购买历史
                    if (securityService.existsUserPurchaseHistory(userId)) {
                        // 5.1.1 询用户购买历史，生成推荐奖品
                        List<UserPurchaseHistoryBO> userPurchaseHistoryBOList = securityService.findUserPurchaseHistory(userId);
                        noAwardIdAwardBOS = recommendService.recommendAwardByUserPurchaseHistory(
                                "你是一个推荐系统，根据用户的购买历史推荐最能吸引该用户的商品。",
                                userPurchaseHistoryBOList
                        );
                    } else {
                        // 5.1.2 无购买历史，从海量用户的购买历史中生成热销产品
                        List<UserPurchaseHistoryBO> recentPurchaseHistoryList = securityService.findRecentPurchaseHistory();
                        noAwardIdAwardBOS = recommendService.recommendHotSaleProductByRecentPurchaseHistory(
                                "你是一个推荐系统，根据海量用户的购买历史，推算出近期用户喜好，给某个用户推荐商品。",
                                recentPurchaseHistoryList
                        );
                    }

                    // 5.2 插入数据库
                    StrategyBO strategyBO = raffleArmory.insertAwardList(userId, activityId, noAwardIdAwardBOS);
                    raffleArmory.insertUserRaffleConfig(userId, activityId, strategyBO.getStrategyId());

                    // 5.3 如果该用户没有活动账户，则初始化一个活动账户
                    activityService.initActivityAccount(userId, activityId);
                    // 5.4 如果该用户没有返利账户，则初始化一个返利账户
                    rewardService.initRewardAccount(userId, activityId);

                    // 5.5 装配
                    Long strategyId = raffleArmory.findStrategyIdByActivityIdAndUserId(activityId, userId);
                    raffleArmory.assembleRaffleWeightRandomByStrategyId2(strategyId);  // 装配该策略所需的所有权重对象Map
                    raffleArmory.assembleAllAwardCountByStrategyId(strategyId);  // 装配该策略所需的所有奖品的库存Map
                    raffleArmory.assembleAwardList(strategyId);  // 装配该策略所需的所有奖品列表
                    raffleArmory.insertUserIdActivityIdBloomFilter(userId, activityId);

                    return true;
                }, myScheduledThreadPool);
                StpUtil.getSession().set("isNewUser", true);
                StpUtil.getSession().set("doLoginCompletableFuture", doLoginCompletableFuture);
            }
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
        aoContext.setSaSession(StpUtil.getSession());
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
