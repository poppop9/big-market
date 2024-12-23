package app.xlog.ggbond.security.config;

import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.SaLoginModel;
import org.springframework.stereotype.Component;

/**
 * 安全领域 - SaToken 监听器
 */
@Component
public class MySaTokenListener extends SaTokenListenerForSimple {

    /**
     * 每次登录时触发
     *
     * @param loginType  登录类型
     * @param loginId    userId
     * @param tokenValue token
     * @param loginModel 登录设备
     */
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
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
