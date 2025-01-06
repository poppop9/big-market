package app.xlog.ggbond.raffleAndSecurity;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.raffle.service.IRaffleDispatch;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 抽奖领域 + 安全领域 - 应用服务
 */
@Slf4j
@Service
public class RaffleSecurityAppService {

    @Resource
    private IRaffleArmory raffleArmory;
    @Resource
    private IRaffleDispatch raffleDispatch;
    @Resource
    private ISecurityService securityService;

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
        Long strategyId = securityService.findStrategyIdByActivityIdAndUserId(activityId, userId);

        Long awardId = raffleDispatch.getAwardId(
                activityId,
                strategyId,
                app.xlog.ggbond.raffle.model.bo.UserBO.builder()
                        .userId(userId)
                        .isBlacklistUser(securityService.isBlacklistUser(userId))
                        .raffleTime(securityService.queryRaffleTimesByUserId(userId, strategyId))
                        .build()
        );

        log.atInfo().log(
                "抽奖领域 - " + securityService.getLoginIdDefaultNull() + " 抽到 {} 活动的 {} 奖品", activityId, awardId
        );

        return awardId;
    }

    /**
     * 抽奖领域 - 查询所有中奖记录
     */
    public List<UserRaffleHistoryBO> findAllWinningAwards(Long activityId) {
        // 自动获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        List<UserRaffleHistoryBO> winningAwards = securityService.findWinningAwardsInfo(activityId, user.getUserId());

        return winningAwards;
    }

    /**
     * 安全领域 - 登录
     */
    public void doLogin(Long userId, String password) throws Exception {
        boolean isSuccess = securityService.doLogin(userId, password);
        // 1. 判断是否登录成功
        if (!isSuccess) {
            throw new Exception("用户名或密码错误");
        }

        // 2. 把activityId塞到session里，后面的操作都可以直接从这里取
        String activityId = SaHolder.getRequest().getParam("activityId");
        StpUtil.getSession().set("activityId", activityId);

        // 3. 装配
        Long strategyId = securityService.findStrategyIdByActivityIdAndUserId(Long.valueOf(activityId), userId);
        raffleArmory.assembleRaffleWeightRandomByStrategyId2(strategyId);  // 装配该策略所需的所有权重对象Map
        raffleArmory.assembleAllAwardCountByStrategyId(strategyId);  // 装配该策略所需的所有奖品的库存Map

        // 4. 将该用户的角色信息放入session
        securityService.insertPermissionIntoSession();
    }

}
