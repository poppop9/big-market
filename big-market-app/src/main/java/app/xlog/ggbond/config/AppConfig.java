package app.xlog.ggbond.config;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class AppConfig {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private SpringContextUtil springContextUtil;

    @PostConstruct
    public void init() {
        springContextUtil.setApplicationContext(applicationContext);
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

}
