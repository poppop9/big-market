package app.xlog.ggbond.config;

import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private SpringContextUtil springContextUtil;

    @PostConstruct
    public void init(){
        springContextUtil.setApplicationContext(applicationContext);
    }
}
