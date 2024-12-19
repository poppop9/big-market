package app.xlog.ggbond.security.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全领域 - 路由拦截鉴权
 */
@Configuration
public class STPRouteInterceptor implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    SaRouter.match("/api/**")
                            .notMatch("/api/security/user/v1/doLogin")
                            .notMatch("/api/test/v1/isTokenExpired")
                            .check(() -> StpUtil.checkRoleOr("admin", "user", "guest"));
                }))
                .addPathPatterns("/api/**");  // 拦截所有请求
    }

}
