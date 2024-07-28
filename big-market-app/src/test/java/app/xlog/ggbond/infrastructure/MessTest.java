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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class MessTest {
    @Autowired
    private AwardMapper awardMapper;
    @Autowired
    private StrategyMapper strategyMapper;

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
}
