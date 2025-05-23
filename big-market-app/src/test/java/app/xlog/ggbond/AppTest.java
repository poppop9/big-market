package app.xlog.ggbond;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class AppTest {

    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;

    // 测试redisson能不能将java对象存储进redis，而且能够取出之后还能使用
    @Test
    public void testRedissonStorageObject() {
        List<AwardBO> awardBOs = raffleArmoryRepo.findAwardsByStrategyId(10001L);
        List<Long> awardIds = awardBOs.stream()
                .map(AwardBO::getAwardId)
                .toList();

        List<WeightRandom.WeightObj<Long>> weightList = Stream.of(
                new WeightRandom.WeightObj<>(awardIds.get(0), 20),
                new WeightRandom.WeightObj<>(awardIds.get(1), 30),
                new WeightRandom.WeightObj<>(awardIds.get(2), 40),
                new WeightRandom.WeightObj<>(awardIds.get(3), 10)
        ).toList();

        // 拿到权重对象了，我要把它存到redis中
        WeightRandom<Long> wr = RandomUtil.weightRandom(weightList);

        RBucket<Object> bucket = redissonClient.getBucket("test_stroage_object");
        bucket.set(wr);

        WeightRandom wr2 = (WeightRandom) bucket.get();
        System.out.println(wr2.next());
    }

    // 测试redisson
    @Test
    public void testRedisson() {
        RKeys keys = redissonClient.getKeys();
        // 获取所有key值
        keys.getKeys().forEach(System.out::println);
        System.out.println("====================================");

        // 模糊获取key值
        keys.getKeysByPattern("*sys*").forEach(System.out::println);

        // 删除key
        keys.delete("sys1111", "2222_sys2222");

        // 判断key是否存在
        System.out.println(keys.countExists("awards"));

        // 获取key的数量
        System.out.println(keys.count());
    }

    // 测试redisson，奖品
    @Test
    public void testRedissonAwards() {
        RMap<String, String> rMap = redissonClient.getMap("awards");
        rMap.put("101", "随机积分");
        rMap.put("102", "淘宝优惠券");
        rMap.put("103", "京东优惠券");

        // 通过key获取value
        System.out.println(redissonClient.getMap("awards").get("103"));
    }

    // 测试权重算法
    @Test
    public void testRandomWeight() {
        // 构建权重对象WeightObj，将其加入list集合
        List<WeightRandom.WeightObj<String>> weightList = Stream.of(
                new WeightRandom.WeightObj<>("A", 20),
                new WeightRandom.WeightObj<>("B", 30),
                new WeightRandom.WeightObj<>("C", 40),
                new WeightRandom.WeightObj<>("D", 10)
        ).collect(Collectors.toList());

        // 生成权重随机对象
        WeightRandom<String> wr = RandomUtil.weightRandom(weightList);

        int numA = 0, numB = 0, numC = 0, numD = 0;

        // 打印随机结果
        for (int i = 0; i < 1000; i++) {
            switch (wr.next()) {
                case "A":
                    numA++;
                    break;
                case "B":
                    numB++;
                    break;
                case "C":
                    numC++;
                    break;
                case "D":
                    numD++;
                    break;
            }
        }

        System.out.println("A:" + numA);
        System.out.println("B:" + numB);
        System.out.println("C:" + numC);
        System.out.println("D:" + numD);
    }

    @Test
    void name() {
        Scanner in1 = new Scanner(System.in);

        int count = in1.nextInt();
        List<Long> list = new ArrayList<>();
        while (in1.hasNextLong()) {
            list.add(in1.nextLong());
        }

        long all = list.get(0);
        for (int i = 1; i < count; i++) {
            Long i1 = list.get(i);
            all = all * i1;
            if (all >= 1000000000L) {
                System.out.println("TLE");
                return;
            }
        }
        if (all >= 1000000000L) {
            System.out.println("TLE");
        } else {
            System.out.println(all);
        }
    }
}
