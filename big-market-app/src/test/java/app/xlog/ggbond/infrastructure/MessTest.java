package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.mapper.AwardMapper;
import app.xlog.ggbond.persistent.mapper.StrategyMapper;
import app.xlog.ggbond.persistent.po.Strategy;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
public class MessTest {
    private static final Logger log = LoggerFactory.getLogger(MessTest.class);
    @Autowired
    private AwardMapper awardMapper;
    @Autowired
    private StrategyMapper strategyMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private IRaffleRepository strategyRepository;
    @Resource
    private IAwardInventoryRepository awardInventoryRepository;
    @Autowired
    private ObjectMapper objectMapper;

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

    // 测试AtomicLong
    @Test
    public void test_getAtomicLong() {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong("testAtomicLong");

        rAtomicLong.set(100);
        log.atInfo().log("当前值: {}", rAtomicLong.get());

        rAtomicLong.incrementAndGet();
        log.atInfo().log("自增后的值: {}", rAtomicLong.get());

        rAtomicLong.decrementAndGet();
        log.atInfo().log("自减后的值: {}", rAtomicLong.get());

        long l = rAtomicLong.addAndGet(-100);
        log.atInfo().log("加-100后的值: {}", l);
    }

    // 测试添加信息到队列中
    @Test
    public void test_addQueue() {
        for (int i = 0; i < 5; i++) {
            awardInventoryRepository.addDecrAwardCountToQueue(
                    DecrQueueVO.builder()
                            .strategyId(10001)
                            .awardId(101)
                            .build()
            );
        }
    }

    // 测试从队列中获取数据
    @Test
    public void test_getQueue() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            DecrQueueVO decrQueueVO = awardInventoryRepository.queryDecrAwardCountFromQueue();
            System.out.println(decrQueueVO);
            System.out.println(decrQueueVO.getStrategyId());
            System.out.println(decrQueueVO.getAwardId());

            Thread.sleep(1000);
        }
    }

    @Test
    public void test_Optional() {
//        Optional<String> optionalS = Optional.of("Hello world");
//        optionalS.map(String::length)

        Optional<String> optional = Optional.empty();
        System.out.println(optional.orElse("为空为空 ！！！"));

        optional.or(() -> Optional.of("Hello world"));

    }
}
