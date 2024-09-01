package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.domain.StrategyArmoryDispatchTest;
import app.xlog.ggbond.raffle.model.AwardBO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.ListAddListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

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
    }

    /**
     * 测试如果redis中没有对应的key，那查出来是null，还是有对象但是里面为null
     **/
    @Test
    void test_getRList() {
        RBucket<Object> bucket = redissonClient.getBucket("null");
        boolean empty = bucket.isExists();
        System.out.println(empty);
    }
}
