package app.xlog.ggbond.security.config;

import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 安全领域 - SaToken 监听器的实现
 */
@Component
public class MySaTokenListener extends SaTokenListenerForSimple {

    @Resource
    private IRaffleArmory raffleArmory;
    @Resource
    private ISecurityRepo securityRepo;

    /**
     * 每次登录时触发
     * todo 未测试
     *
     * @param loginType  登录类型
     * @param loginId    userId
     * @param tokenValue token
     * @param loginModel 登录设备
     */
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        // 装配该用户相关的权重对象，以及所有奖品的库存
        String activityId = SaHolder.getRequest().getParam("activityId");
        Long strategyId = securityRepo.findStrategyIdByActivityIdAndUserId(Long.valueOf(activityId), Long.valueOf(loginId.toString()));

        // 装配该策略所需的所有权重对象
        raffleArmory.assembleRaffleWeightRandomByStrategyId(strategyId);
        // 装配该策略所需的所有奖品的库存
        raffleArmory.assembleAllAwardCountBystrategyId(strategyId);
    }

    /**
     * 每次被踢下线时触发
     */
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        // todo 踢人下线时应该清除该用户的权重对象，以及所有奖品的库存
        System.out.println("---------- 自定义侦听器实现 doKickout");
        // Set<String> tokenSet = StpUtil.searchTokenValue("", 0, -1);

    }

    // todo 如果这个人的 token 失效了，说明很久没有登录了，那就要清除该用户的权重对象，以及所有奖品的库存

}
