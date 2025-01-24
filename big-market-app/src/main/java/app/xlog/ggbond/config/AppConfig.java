package app.xlog.ggbond.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")  // 允许的http方法
                        .allowedHeaders("authorization", "content-type");  // 允许一些特殊的请求头
            }
        };
    }

    /**
     * 更改参数校验器的异常（默认抛出的异常是ConstraintViolationException，不够详细）
     */
    @Bean
    public static MethodValidationPostProcessor validationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setAdaptConstraintViolations(true);
        return processor;
    }

    /**
     * OKHttp 请求客户端配置
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

}
