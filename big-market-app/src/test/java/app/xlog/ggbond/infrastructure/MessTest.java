package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.mapper.AwardMapper;
import app.xlog.ggbond.persistent.mapper.StrategyMapper;
import app.xlog.ggbond.persistent.po.Award;
import app.xlog.ggbond.persistent.po.Strategy;
import app.xlog.ggbond.strategy.model.AwardBO;
import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class MessTest {
    private static final Logger log = LoggerFactory.getLogger(MessTest.class);
    @Autowired
    private AwardMapper awardMapper;
    @Autowired
    private StrategyMapper strategyMapper;
    @Resource
    private RedissonClient redissonClient;

    // Jackson对象
    @Autowired
    private ObjectMapper objectMapper;

    // 测试hutool的BeanUtil.copyProperties方法
    @Test
    public void testHutoolCopyProperties() {
        Award award = Award.builder()
                .id(1)
                .strategyId(1)
                .awardId(101)
                .awardKey("random_points")
                .awardConfig("random_points_config")
                .awardTitle("随机积分")
                .awardSubtitle("随机积分副标题")
                .awardCount(100)
                .awardRate(0.1f)
                .awardSort(1)
                .rules("Collections.emptyMap()")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        AwardBO awardBO = new AwardBO();

        BeanUtil.copyProperties(award, awardBO);
        System.out.println(awardBO);
    }

    // 测试hutool的BeanUtil的copyToList方法
    @Test
    public void testcopyToList() {
        List<Award> awards = Stream.of(
                Award.builder()
                        .id(1).strategyId(1).awardId(101)
                        .awardKey("random").awardConfig("random")
                        .awardTitle("随机积分").awardSubtitle("副标题")
                        .awardCount(100).awardRate(0.1f).awardSort(1)
                        .rules("Collections.emptyMap()").createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build(),
                Award.builder()
                        .id(2).strategyId(1).awardId(102)
                        .awardKey("random").awardConfig("config")
                        .awardTitle("随机积分").awardSubtitle("副")
                        .awardCount(100).awardRate(0.1f)
                        .awardSort(1).rules("Collections.emptyMap()")
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build()
        ).toList();

        List<AwardBO> awardBOs = BeanUtil.copyToList(awards, AwardBO.class);
        awardBOs.forEach(System.out::println);
    }

    // 测试数据库中的json数据类型，怎么映射到java的map集合上
    @Test
    public void testJsonToMap() throws JsonProcessingException {
        // 查出所有的strategy
        List<Strategy> strategies = strategyMapper.selectList(null);
        // 只需要strategie属性中的rule，rules是所有规则字符串的集合
        List<String> rules = strategies.stream()
                .map(Strategy::getRules)
                .toList();
        rules.forEach(System.out::println);

        System.out.println("======================================");

        // 一条规则是一条json数据
        for (String rule : rules) {
            Map<String, String> rulesMap = objectMapper.readValue(
                    rule,
                    new TypeReference<Map<String, String>>() {
                    }
            );

            System.out.println(rulesMap.get("rule_weight"));
            System.out.println(rulesMap.get("rule_blacklist"));
        }
    }

    // 测试toList
    @Test
    public void test_toList() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        List<String> list1 = list.stream()
                .toList();

        List<String> collect = list1.stream()
                .collect(Collectors.toList());
    }

    // 测试布隆过滤器
    @Test
    public void test_BloomFilter() {
        RBloomFilter<Object> rBloomFilter = redissonClient.getBloomFilter("testBloomFilter");

        rBloomFilter.tryInit(1000, 0.01);
        rBloomFilter.add("100");
        rBloomFilter.add("200");
        rBloomFilter.add("300");

        log.atInfo().log("是否包含100: {}", rBloomFilter.contains("100"));
        log.atInfo().log("是否包含200: {}", rBloomFilter.contains("200"));
        log.atInfo().log("是否包含300: {}", rBloomFilter.contains("300"));
        log.atInfo().log("是否包含400: {}", rBloomFilter.contains("400"));
    }
}
