package app.xlog.ggbond.security.config;

import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.SaLoginModel;
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
     * todo 当登录时，存储token，然后后期获取所有可用token，判断差值，然后清理权重对象
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
     * 每次注销时触发
     */
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        // token失效，或被冻结，不会触发
        System.out.println("---------- 自定义侦听器实现 doLogout");
    }

    /**
     * 每次被封禁时触发
     */
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        System.out.println("---------- 自定义侦听器实现 doDisable");
    }

    /**
     * 每次注销 Session 时触发
     *
     * @param id session的id
     */
    public void doLogoutSession(String id) {
        // token失效，或被冻结，不会触发
        System.out.println("---------- 自定义侦听器实现 session 注销了：" + id);
    }

}
