package app.xlog.ggbond.domain;

import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.compiler.ast.NewExpr;
import org.junit.jupiter.api.Test;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.ListAddListener;
import org.redisson.api.listener.TrackingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {

    private static final Logger log = LoggerFactory.getLogger(RedissonTest.class);
    @Resource
    private RedissonClient redissonClient;

    /*
    测试队列
     */
    @Test
    public void test_QueueListener() throws InterruptedException {
        RQueue<Object> rQueue = redissonClient.getQueue("Queue");

        int listener = rQueue.addListener(
                new ListAddListener() {
                    @Override
                    public void onListAdd(String s) {
                        log.atInfo().log("队列添加了元素: {}", s);
                    }
                }
        );

        rQueue.add("test1");
        rQueue.add("test2");
        rQueue.add(new Object());

//        RDelayedQueue<String> rDelayedQueue = redissonClient.getDelayedQueue(rQueue);
//
//        for (int i = 0; i < 10; i++) {
//            rDelayedQueue.offer("test", 4, java.util.concurrent.TimeUnit.SECONDS);
//            Thread.sleep(5000);
//        }
//
//        // 取出元素
//        for (int j = 0; j < 10; j++) {
//            System.out.println(rQueue.poll());
//        }
    }


}
