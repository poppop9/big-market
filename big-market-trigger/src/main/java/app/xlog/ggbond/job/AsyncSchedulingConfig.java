package app.xlog.ggbond.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * 异步定时任务配置类
 */
@EnableAsync
@EnableScheduling
@Configuration
public class AsyncSchedulingConfig {

    /**
     * 定时调度线程池
     */
    @Bean(name = "myScheduledThreadPool")
    public ThreadPoolTaskScheduler myScheduledThreadPool() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(50); // 设置线程池大小
        scheduler.setThreadNamePrefix("MyScheduledThreadPool - ");
        scheduler.initialize();

        return scheduler;
    }

    /**
     * 异步执行线程池
     */
    @Bean(name = "myAsyncExecutorThreadPool")
    public Executor myAsyncExecutorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);  // 核心线程数
        executor.setMaxPoolSize(50);  // 最大线程数
        executor.setQueueCapacity(100);  // 队列容量
        executor.setThreadNamePrefix("MyAsyncExecutorThreadPool - ");
        executor.initialize();

        return executor;
    }

}
