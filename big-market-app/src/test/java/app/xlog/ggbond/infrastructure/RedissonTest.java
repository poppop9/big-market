package app.xlog.ggbond.infrastructure;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.ListAddListener;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RedissonTest {

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

    /**
     * 测试布隆过滤器
     */
    @Test
    void test_849() {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter("BlacklistUserList");
        System.out.println(bloomFilter.contains(404L));
        System.out.println(bloomFilter.contains(404L));
        System.out.println(bloomFilter.contains(101L));
        System.out.println(bloomFilter.contains(101L));
    }

}
