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
     * 初始化用户
     */
    @Test
    void test_4() {
        userRepository.saveAll(List.of(
                User.builder()
                        .userId(404L)
                        .userName("404用户")
                        .userRole(User.UserRole.BLACKLIST)
                        .build(),
                User.builder()
                        .userId(111L)
                        .userName("管理员")
                        .userRole(User.UserRole.ADMIN)
                        .build(),
                User.builder()
                        .userId(222L)
                        .userName("普通用户")
                        .userRole(User.UserRole.USER)
                        .build()
        ));
    }

    /**
     * 初始化抽奖池规则
     */
    @Test
    void test_5() {
        rafflePoolRepository.saveAll(List.of(
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L, 102L, 103L, 104L, 105L, 106L, 107L, 108L, 109L))
                        .rafflePoolType(RafflePool.RafflePoolType.NormalTime)
                        .rafflePoolName("AllAwardPool")
                        .normalTimeStartValue(20L)
                        .normalTimeEndValue(Long.MAX_VALUE)
                        .ruleDescription("所有奖品。抽奖次数大于等于 20 时")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L, 102L, 103L, 104L, 105L, 106L, 107L, 108L))
                        .rafflePoolType(RafflePool.RafflePoolType.NormalTime)
                        .rafflePoolName("No1stAwardPool")
                        .normalTimeStartValue(10L)
                        .normalTimeEndValue(19L)
                        .ruleDescription("没有 109 大奖。抽奖次数在 10-19 次时")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L, 102L, 103L, 104L, 105L))
                        .rafflePoolType(RafflePool.RafflePoolType.NormalTime)
                        .rafflePoolName("No1stAnd2ndAwardPool")
                        .normalTimeStartValue(0L)
                        .normalTimeEndValue(9L)
                        .ruleDescription("没有 105，106，107，108，109 大奖。抽奖次数在 0-9 次时")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(106L, 107L, 108L, 109L))
                        .rafflePoolType(RafflePool.RafflePoolType.SpecialTime)
                        .rafflePoolName("1stAnd2ndAwardPool")
                        .specialTimeValue(50L)
                        .ruleDescription("都是一二级的大奖。抽奖第 50 次，必中大奖")
                        .build(),
                RafflePool.builder()
                        .strategyId(10001L)
                        .awardIds(List.of(101L))
                        .rafflePoolType(RafflePool.RafflePoolType.SpecialRule)
                        .rafflePoolName("BlacklistPool")
                        .ruleDescription("黑名单用户专属抽奖池")
                        .build()
        ));
    }
}
