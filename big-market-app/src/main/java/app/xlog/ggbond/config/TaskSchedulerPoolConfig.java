package app.xlog.ggbond.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务线程池配置
 */
@Configuration
public class TaskSchedulerPoolConfig {
    /**
     * 定时调度异步线程池
     */
    @Bean(name = "myScheduledThreadPool")
    public ThreadPoolTaskScheduler myScheduledThreadPool() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(16); // 设置线程池大小
        scheduler.setThreadNamePrefix("MyScheduledThreadPool - ");
        scheduler.initialize();
        return scheduler;
    }
}