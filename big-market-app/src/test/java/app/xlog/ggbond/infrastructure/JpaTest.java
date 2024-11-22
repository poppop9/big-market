package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import app.xlog.ggbond.persistent.po.security.User;
import app.xlog.ggbond.persistent.repository.jpa.AwardRepository;
import app.xlog.ggbond.persistent.repository.jpa.RafflePoolRepository;
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
    private RafflePoolRepository rafflePoolRepository;

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
/*    @Test
    void test_4() {
        raffleRuleRepository.saveAll(List.of(
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Dispatch)
                        .strategyId(10001L)
                        .awardId(101L)
                        .ruleKey(RafflePool.RuleKey.rule_blacklist)
                        .ruleGrade(40).ruleDescription("黑名单用户专属奖品")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(106L)
                        .ruleKey(RafflePool.RuleKey.rule_lock).ruleValue(10L)
                        .ruleGrade(56).ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(106L)
                        .ruleKey(RafflePool.RuleKey.rule_grand).ruleValue(50L)
                        .ruleGrade(49).ruleDescription("当用户抽奖第50次时，将获得一个rule_grand抽奖池里的奖品")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(107L)
                        .ruleKey(RafflePool.RuleKey.rule_lock).ruleValue(10L)
                        .ruleGrade(56).ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(107L)
                        .ruleKey(RafflePool.RuleKey.rule_grand).ruleValue(50L)
                        .ruleGrade(49).ruleDescription("当用户抽奖第50次时，将获得一个rule_grand抽奖池里的奖品")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(108L)
                        .ruleKey(RafflePool.RuleKey.rule_lock).ruleValue(10L)
                        .ruleGrade(56).ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(108L)
                        .ruleKey(RafflePool.RuleKey.rule_grand).ruleValue(50L)
                        .ruleGrade(49).ruleDescription("当用户抽奖第50次时，将获得一个rule_grand抽奖池里的奖品")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(109L)
                        .ruleKey(RafflePool.RuleKey.rule_lock).ruleValue(20L)
                        .ruleGrade(56).ruleDescription("该奖品需要抽奖次数达到某个值，才能抽取")
                        .build(),
                RafflePool.builder()
                        .ruleType(RafflePool.RuleType.Limit)
                        .strategyId(10001L)
                        .awardId(109L)
                        .ruleKey(RafflePool.RuleKey.rule_grand).ruleValue(50L)
                        .ruleGrade(49).ruleDescription("当用户抽奖第50次时，将获得一个rule_grand抽奖池里的奖品")
                        .build()
        ));
    }*/

    /**
     * 初始化抽奖池规则
     */
    @Test
    void test_5() {
        rafflePoolRepository.saveAll(List.of(
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L, 102L, 103L, 104L, 105L,106L, 107L, 108L, 109L))
                        .rafflePoolName("10001 - 全奖品抽奖池")
                        .ruleDescription("有所有奖品")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L))
                        .rafflePoolName("10001 - 黑名单抽奖池")
                        .ruleDescription("黑名单用户专属抽奖池")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(106L, 107L, 108L, 109L))
                        .rafflePoolName("10001 - 一二级大奖抽奖池")
                        .ruleDescription("都是一二级的大奖")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L, 102L, 103L, 104L, 105L, 106L, 107L, 108L))
                        .rafflePoolName("10001 - 无一级抽奖池")
                        .ruleDescription("没有 109 大奖")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L, 102L, 103L, 104L, 105L))
                        .rafflePoolName("10001 - 无一二级抽奖池")
                        .ruleDescription("没有 105，106，107，108，109 大奖")
                        .build()
                ));
    }
}
