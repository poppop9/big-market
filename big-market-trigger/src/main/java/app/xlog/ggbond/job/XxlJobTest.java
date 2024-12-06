package app.xlog.ggbond.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class XxlJobTest {

    @XxlJob("testJob")
    public void execute() throws InterruptedException {
        System.out.println("xxl-job test in thread: " + Thread.currentThread().getName());
        XxlJobHelper.log("xxl-job test in thread: " + Thread.currentThread().getName());
        //Thread.sleep(10000);  // 10s
        XxlJobHelper.handleSuccess();
    }

}
