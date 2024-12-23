package app.xlog.ggbond.job;

import cn.dev33.satoken.stp.StpUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 测试定时任务
 */
@Component
public class JobTest {

    int count = 0;

    @XxlJob("testJob")
    public void execute() {
        System.out.println("xxl-job test in thread: " + Thread.currentThread().getName());
        XxlJobHelper.log("xxl-job test in thread: " + Thread.currentThread().getName());
        // Thread.sleep(10000);  // 10s
        XxlJobHelper.handleSuccess();
    }

    @Scheduled(fixedDelay = 1000)
    public void test() {
        // token 失效后大概20秒会搜不到
        List<String> strings = StpUtil.searchTokenValue("", 0, 1000, true);
        strings.forEach(item -> {
            System.out.println(count++ + " : " + item);
        });
        System.out.println("-------------------");
    }

}
