package app.xlog.ggbond;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class AppTest {
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
