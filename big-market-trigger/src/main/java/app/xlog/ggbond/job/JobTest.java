package app.xlog.ggbond.job;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.vo.QueueItemVO;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 测试任务
 */
@Component
public class JobTest {

    @Resource
    private RedissonClient redissonClient;

    int count = 0;

    @XxlJob("test_job")
    public void execute() {
        System.out.println("xxl-job test in thread: " + Thread.currentThread().getName());
        XxlJobHelper.log("xxl-job test in thread: " + Thread.currentThread().getName());
        // Thread.sleep(10000);  // 10s
        XxlJobHelper.handleSuccess();
    }

    // @Scheduled(fixedDelay = 1000)
    public void test() {
        // token 失效后大概20秒会搜不到
        List<String> strings = StpUtil.searchTokenValue("", 0, 1000, true);
        strings.forEach(item -> {
            System.out.println(count++ + " : " + item);
        });
        System.out.println("-------------------");
    }

    // @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void testRDelayedQueue() {
        RQueue<QueueItemVO> rQueue = redissonClient.getQueue(GlobalConstant.RedisKey.CHECK_EXPIRE_PENDING_PAYMENT_AO_QUEUE);
        RDelayedQueue<QueueItemVO> rDelayedQueue = redissonClient.getDelayedQueue(rQueue);

        QueueItemVO poll = rDelayedQueue.poll();
        System.out.println(poll);
    }

}
