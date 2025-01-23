package app.xlog.ggbond.integrationService;

import app.xlog.ggbond.BigMarketException;
import app.xlog.ggbond.BigMarketRespCode;
import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.service.IActivityService;
import app.xlog.ggbond.activity.service.statusFlow.AOEventCenter;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.StrategyBO;
import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.raffle.service.IRaffleDispatch;
import app.xlog.ggbond.recommend.RecommendService;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleHistoryBO;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 组合应用服务
 */
@Slf4j
@Service
public class TriggerService {

    @Resource
    private IActivityService activityService;
    @Resource
    private IRaffleArmory raffleArmory;
    @Resource
    private IRaffleDispatch raffleDispatch;
    @Resource
    private ISecurityService securityService;
    @Resource
    private RecommendService recommendService;
    @Resource
    private AOEventCenter aoEventCenter;

    /**
     * 抽奖领域 - 查询当前用户在指定活动下的所有奖品
     */
    public List<AwardBO> findAllAwardsByActivityIdAndCurrentUser(Long activityId) {
        // 自动获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        List<AwardBO> awardBOs = raffleArmory.findAllAwards(activityId, user.getUserId());
        log.atDebug().log("查询了活动 {} ，用户 {} 的奖品列表", activityId, user.getUserId());

        return awardBOs;
    }

    /**
     * 抽奖领域 - 根据活动id和当前用户，抽取一个奖品id
     */
    public Long dispatchAwardIdByActivityIdAndCurrentUser(Long activityId) {
        // 自动获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        Long userId = user.getUserId();
        // 跟据活动id，用户id，查询用户的策略id
        Long strategyId = raffleArmory.findStrategyIdByActivityIdAndUserId(activityId, userId);

        Long awardId = raffleDispatch.getAwardId(
                strategyId,
                app.xlog.ggbond.raffle.model.bo.UserBO.builder()
                        .userId(userId)
                        .isBlacklistUser(securityService.isBlacklistUser(userId))
                        .build()
        );

        log.atInfo().log("抽奖领域 - " +
                securityService.getLoginIdDefaultNull() + " 抽到 {} 活动的 {} 奖品", activityId, awardId
        );

        return awardId;
    }

    /**
     * 抽奖领域 - 查询所有中奖记录
     */
    public List<UserRaffleHistoryBO> findAllWinningAwards(Long activityId) {
        // 自动获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        return raffleArmory.findWinningAwardsInfo(activityId, user.getUserId());
    }

    /**
     * 安全领域 - 登录
     */
    public void doLogin(Long userId, String password) {
        boolean isSuccess = securityService.doLogin(userId, password);
        // 1. 判断是否登录成功
        if (!isSuccess) {
            throw new BigMarketException(BigMarketRespCode.WRONG_USERNAME_OR_PASSWORD);
        }

        // 2. 把activityId塞到session里，后面的操作都可以直接从这里取
        long activityId = Long.parseLong(SaHolder.getRequest().getParam("activityId"));
        StpUtil.getSession().set("activityId", activityId);

        // 3. 检查该用户是否有策略，如果没有则ai生成推荐商品
        if (!securityService.existsByUserIdAndActivityId(activityId, userId)) {
            List<AwardBO> noAwardIdAwardBOS;
            // 3.1 判断用户是否有购买历史
            if (securityService.existsUserPurchaseHistory(userId)) {
                // 3.1.1 查询用户购买历史，生成推荐奖品
                List<UserPurchaseHistoryBO> userPurchaseHistoryBOList = securityService.findUserPurchaseHistory(securityService.getLoginIdDefaultNull());
                noAwardIdAwardBOS = recommendService.recommendAwardByUserPurchaseHistory(
                        "你是一个推荐系统，根据用户的购买历史推荐最能吸引该用户的商品。",
                        userPurchaseHistoryBOList
                );
            } else {
                // 3.1.2 无购买历史，从海量用户的购买历史中生成热销产品
                List<UserPurchaseHistoryBO> recentPurchaseHistoryList = securityService.findRecentPurchaseHistory();
                noAwardIdAwardBOS = recommendService.recommendHotSaleProductByRecentPurchaseHistory(
                        "你是一个推荐系统，根据海量用户的购买历史，推算出近期用户喜好，给某个用户推荐商品。",
                        recentPurchaseHistoryList
                );
            }

            // 3.2 插入数据库
            StrategyBO strategyBO = raffleArmory.insertAwardList(userId, activityId, noAwardIdAwardBOS);
            raffleArmory.insertUserRaffleConfig(userId, activityId, strategyBO.getStrategyId());
        }

        // 4. 如果该用户没有活动账户，则初始化一个活动账
        activityService.initActivityAccount(userId, activityId);

        // 5. 装配
        Long strategyId = raffleArmory.findStrategyIdByActivityIdAndUserId(activityId, userId);
        raffleArmory.assembleRaffleWeightRandomByStrategyId2(strategyId);  // 装配该策略所需的所有权重对象Map
        raffleArmory.assembleAllAwardCountByStrategyId(strategyId);  // 装配该策略所需的所有奖品的库存Map

        // 6. 将该用户的角色信息放入session
        securityService.insertPermissionIntoSession();
    }

    /**
     * 活动领域 - 充值待支付活动单
     */
    public ActivityOrderBO rechargePendingPaymentAO(Long activityId, String activityOrderTypeName) {
        ActivityOrderBO activityOrderBO = aoEventCenter.publishInitialToPendingPaymentEvent(AOContext.builder()
                .userId(securityService.getLoginIdDefaultNull())
                .activityId(activityId)
                .activityOrderType(ActivityOrderTypeBO.builder()
                        .activityOrderTypeName(ActivityOrderTypeBO.ActivityOrderTypeName.valueOf(activityOrderTypeName))
                        .build()
                )
                .build()
        );
        return activityOrderBO;
    }

}
