package app.xlog.ggbond.config;

import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    /**
     * 跨域配置
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**")  // 应用到所有请求
                        .allowedOrigins("http://localhost:5173")  // 允许的地址
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD", "TRACE")  // 允许的http方法
                        .allowedHeaders("*")  // 允许的请求头
                        .allowCredentials(true);  // 允许一些特殊的请求头
            }
        };
    }

    /**
     * OKHttp 请求客户端配置
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

}
