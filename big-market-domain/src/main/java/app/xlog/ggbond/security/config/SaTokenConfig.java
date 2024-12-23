package app.xlog.ggbond.security.config;

import org.springframework.context.annotation.Configuration;

/**
 * 安全领域 - SaToken 配置
 */
@Configuration
public class SaTokenConfig {

    /**
     * 重写 Sa-Token 框架内部算法策略
     */
    /*@PostConstruct
    public void rewriteSaStrategy() {
        // 重写 Token 生成策略
        SaStrategy.instance.createToken = (loginId, loginType) -> {
            String userId = SaHolder.getRequest().getParam("userId");

            String uuid = UUID.randomUUID().toString();


            return uuid;
        };
    }*/

}
