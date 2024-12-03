package app.xlog.ggbond.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * 全局 - 定时任务线程池配置类
 */
@EnableScheduling
@Configuration
public class TaskSchedulerPoolConfig {

    /**
     * 定时调度异步线程池
     */
    @Bean(name = "myScheduledThreadPool")
    public ThreadPoolTaskScheduler myScheduledThreadPool() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20); // 设置线程池大小
        scheduler.setThreadNamePrefix("MyScheduledThreadPool - ");
        scheduler.initialize();

        return scheduler;
    }

}
