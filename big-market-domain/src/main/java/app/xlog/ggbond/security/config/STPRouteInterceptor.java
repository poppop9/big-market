package app.xlog.ggbond.security.config;

import app.xlog.ggbond.security.model.UserBO;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.EnumSet;

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
                            .check(() -> StpUtil.checkRoleOr(EnumSet.allOf(UserBO.UserRole.class).stream().map(Enum::name).toArray(String[]::new)));
                }))
                .addPathPatterns("/api/**");  // 拦截所有请求
    }

}
