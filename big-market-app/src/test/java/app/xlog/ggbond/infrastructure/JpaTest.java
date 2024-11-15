package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RaffleRule;
import app.xlog.ggbond.persistent.po.security.User;
import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.persistent.repository.jpa.RaffleRuleRepository;
import app.xlog.ggbond.persistent.repository.jpa.UserRepository;
import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.operator.Between;
import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.operator.Equal;
import cn.zhxu.bs.util.MapUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class JpaTest {

    @Resource
    private BeanSearcher beanSearcher;

    @Resource
    private AwardRepository awardRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private RaffleRuleRepository raffleRuleRepository;

    @Test
    void test_1() {
        awardRepository.findAll(Example.of(Award.builder()
                .strategyId(10001L)
                .awardTitle(null)
                .build())
        ).forEach(System.out::println);
        System.out.println("====================================");

        awardRepository.findByAwardTitleContainsAndAwardCountBetween(null, 10000L, 50000L)
                .forEach(System.out::println);
        System.out.println("====================================");

        awardRepository.findByAwardCountBetween(10000L, 500000L)
                .forEach(System.out::println);
    }

    @Test
    void test_2() {
        String s = null;

        Map<String, Object> map = MapUtils.builder()
                .field("strategyId", 10001).op(Equal.class)
                .field("awardCount", 10000L, 50000L).op(Between.class)
                .field("awardTitle", s).op(Contain.class)
                .build();

        SearchResult<Award> search = beanSearcher.search(Award.class, map);
        System.out.println(Arrays.toString(search.getSummaries()) + " " + search.getTotalCount());
        search.getDataList().forEach(System.out::println);
    }

    @Test
    void test_3() {
        userRepository.save(User.builder()
                .id(452L)
                .userId(10001L)
                .userName("测试33")
                .password("123456")
                .raffleTimes(888L)
                .build()
        );
    }

    /**
     * 初始化RaffleRule表
     */
    @Test
    void test_4() {
        raffleRuleRepository.saveAll(List.of(
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Strategy).strategyOrAwardId(10001L)
                        .ruleKey(RaffleRule.RuleKey.rule_grand).ruleValue(50L)
                        .ruleDescription("当用户抽奖第50次时，将获得一个rule_grand抽奖池里的奖品")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(101L)
                        .ruleKey(RaffleRule.RuleKey.rule_common).ruleValue(-1L)
                        .ruleDescription("该奖品无需任何条件，直接抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(102L)
                        .ruleKey(RaffleRule.RuleKey.rule_common).ruleValue(-1L)
                        .ruleDescription("该奖品无需任何条件，直接抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(103L)
                        .ruleKey(RaffleRule.RuleKey.rule_common).ruleValue(-1L)
                        .ruleDescription("该奖品无需任何条件，直接抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(104L)
                        .ruleKey(RaffleRule.RuleKey.rule_common).ruleValue(-1L)
                        .ruleDescription("该奖品无需任何条件，直接抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(105L)
                        .ruleKey(RaffleRule.RuleKey.rule_common).ruleValue(-1L)
                        .ruleDescription("该奖品无需任何条件，直接抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(106L)
                        .ruleKey(RaffleRule.RuleKey.rule_lock).ruleValue(10L)
                        .ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(107L)
                        .ruleKey(RaffleRule.RuleKey.rule_lock).ruleValue(10L)
                        .ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(108L)
                        .ruleKey(RaffleRule.RuleKey.rule_lock).ruleValue(10L)
                        .ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build(),
                RaffleRule.builder()
                        .ruleType(RaffleRule.RuleType.Award).strategyOrAwardId(109L)
                        .ruleKey(RaffleRule.RuleKey.rule_lock).ruleValue(20L)
                        .ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build()
        ));
    }
}
