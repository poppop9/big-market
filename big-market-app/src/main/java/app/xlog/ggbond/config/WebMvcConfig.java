package app.xlog.ggbond.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置
 */
@Configuration
public class WebMvcConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**")  // 应用到所有请求
                        .allowedOrigins("http://localhost:5173")  // 允许的地址
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")  // 允许的http方法
                        .allowedHeaders("authorization", "content-type");  // 允许一些特殊的请求头
            }
        };
    }
}
