package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.api.listener.ListAddListener;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;

    @Test
    void test_dc9j() {
        RBitSet bitSet = redissonClient.getBitSet("myBitmap");

        // 设置指定位置的位
        bitSet.set(0, true); // 设置第0位为1
        bitSet.set(1, false); // 设置第1位为0
        bitSet.set(2); // 设置第2位为1（简写）
        bitSet.set(1879820345763684352L, true);

        // 获取指定位置的位
        boolean bit0 = bitSet.get(0); // 获取第0位，结果为 true
        boolean bit1 = bitSet.get(1); // 获取第1位，结果为 false

        // 批量设置位
        bitSet.set(10, 15, true); // 将第10位到第14位设置为1

        // 获取范围内的位
        long cardinality = bitSet.cardinality(); // 获取所有置1的位的数量
        long length = bitSet.length(); // 获取 Bitmap 的总长度（最后一位+1）
    }

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

    @Test
    void test_ru48e() {
        RSet<String> electronics = redissonClient.getSet("products:electronics");
    }

    @Test
    void test_ufdjo() {
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet("products:electronics");
        scoredSortedSet.add(10.1, "iphone");
        scoredSortedSet.add(9.9, "huawei");
    }

    @Test
    void test_8u4jf() {
        Map<String, WeightRandom<Long>> map = new HashMap<>();

        raffleArmoryRepo.findAllRafflePoolByStrategyId(10001L)
                .forEach(item -> {
                    List<WeightRandom.WeightObj<Long>> weightObjs = item.getAwardIds().stream()
                            .map(child -> {
                                AwardBO award = raffleArmoryRepo.findAwardByAwardId(child);
                                return new WeightRandom.WeightObj<>(child, award.getAwardRate());
                            })
                            .toList();
                    WeightRandom<Long> weightRandom = RandomUtil.weightRandom(weightObjs);
                    map.put(item.getRafflePoolName(), weightRandom);
                });

        RMap<String, WeightRandom<Long>> rMap = redissonClient.getMap("myMap");
        rMap.putAll(map);
    }

    @Test
    void test_8u4jfdf() {
        Map<String, RafflePoolBO> map = new HashMap<>();

        raffleArmoryRepo.findAllRafflePoolByStrategyId(10001L)
                .forEach(item -> map.put(item.getRafflePoolName(), item));

        RMap<String, RafflePoolBO> rMap = redissonClient.getMap("myMap2");
        rMap.putAll(map);
    }

}
