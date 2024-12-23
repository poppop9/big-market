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
                    // 已经被拦截器拦截到了，接下来要怎么处理
                    SaRouter.match("/api/raffle/assemble/**")
                            .check(() -> StpUtil.checkRoleOr(EnumSet.allOf(UserBO.UserRole.class).stream().map(Enum::name).toArray(String[]::new)));
                    SaRouter.match("/api/raffle/dispatch/**")
                            .check(() -> StpUtil.checkRoleOr(EnumSet.allOf(UserBO.UserRole.class).stream().map(Enum::name).toArray(String[]::new)));
                    SaRouter.match("/api/security/user/**")
                            .check(() -> System.out.println("这里是安全领域的用户权限接口"));
                }))
                .addPathPatterns("/api/**");  // 指定拦截器需要拦截的 URL
    }

}
