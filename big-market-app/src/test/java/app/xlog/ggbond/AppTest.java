package app.xlog.ggbond;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.RKeys;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class AppTest {
    @Autowired
    private RedissonClient redissonClient;

    // 测试redisson
    @Test
    public void testRedisson() {
        RKeys keys = redissonClient.getKeys();
        //获取所有key值
        keys.getKeys().forEach(System.out::println);
        System.out.println("====================================");

        //模糊获取key值
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
                new WeightRandom.WeightObj<String>("A", 20),
                new WeightRandom.WeightObj<String>("B", 30),
                new WeightRandom.WeightObj<String>("C", 40),
                new WeightRandom.WeightObj<String>("D", 10)
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
}
