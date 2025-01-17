package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.po.activity.ActivityOrderFlow;
import app.xlog.ggbond.persistent.po.raffle.*;
import app.xlog.ggbond.persistent.po.security.User;
import app.xlog.ggbond.persistent.po.security.UserPurchaseHistory;
import app.xlog.ggbond.persistent.po.security.UserRaffleConfig;
import app.xlog.ggbond.persistent.repository.jpa.*;
import cn.hutool.core.util.IdUtil;
import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.operator.Between;
import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.operator.Equal;
import cn.zhxu.bs.util.MapUtils;
import jakarta.annotation.Resource;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class JpaTest {

    @Resource
    private BeanSearcher beanSearcher;

    @Resource
    private ActivityJpa activityJPA;
    @Resource
    private ActivityOrderFlowJpa activityOrderFlowJpa;
    @Resource
    private UserPurchaseHistoryJpa userPurchaseHistoryJpa;
    @Resource
    private StrategyJpa strategyJpa;
    @Resource
    private StrategyAwardJpa strategyAwardJpa;
    @Resource
    private AwardJpa awardJpa;
    @Resource
    private UserJpa userJpa;
    @Resource
    private RafflePoolJpa rafflePoolJpa;
    @Resource
    private UserRaffleConfigJpa userRaffleConfigJpa;

    @Test
    void test_9fuc8d() {

    }

    @Test
    void test_fjdslk2() {
        ActivityOrderFlow build = ActivityOrderFlow.builder()
                .userId(10001L)
                .activityId(10001L)
                .strategyId(10001L)
                .activityOrderId(11144L)
                .activityOrderType(ActivityOrderFlow.ActivityOrderType.SIGN_IN_TO_CLAIM)
                .activityOrderStatus(ActivityOrderFlow.ActivityOrderStatus.NOT_USED)
                .build();
        activityOrderFlowJpa.save(build);
    }

    @Test
    void test_7843fgd() {
        for (int i = 0; i < 10; i++) {
            EasyRandomParameters parameters = new EasyRandomParameters()
                    .dateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1))  // 日期范围
                    .timeRange(LocalTime.MIN, LocalTime.MAX)  // 时间范围
                    .stringLengthRange(5, 10)  // 字符串长度范围
                    .collectionSizeRange(1, 10)  // 集合大小范围
                    .randomize(FieldPredicates.named("userRole"), () -> User.UserRole.values()[new Random().nextInt(2)]);  // 自定义随机器

            User user = new EasyRandom(parameters).nextObject(User.class);
            userJpa.save(user);
        }
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
    void test_784239() {
        /*awardRepository.saveAll(List.of(
                Award.builder()
                        .strategyId(10001L)
                        .awardId(101L)
                        .awardTitle("随机积分 - 测试")
                        .awardCount(80000L)
                        .awardRate(74.0)
                        .awardSort(1)
                        .build()
        ));*/
        long snowflakeNextId = IdUtil.getSnowflakeNextId();
        userJpa.saveAll(List.of(
                User.builder()
                        .userId(snowflakeNextId)
                        .userName("普通用户2 - 测试")
                        .password("222")
                        .userRole(User.UserRole.USER)
                        .build(),
                User.builder()
                        .userId(snowflakeNextId)
                        .userName("普通用户2 - 测试")
                        .password("222")
                        .userRole(User.UserRole.USER)
                        .build(),
                User.builder()
                        .userId(snowflakeNextId)
                        .userName("普通用户2 - 测试")
                        .password("222")
                        .userRole(User.UserRole.USER)
                        .build(),
                User.builder()
                        .userId(snowflakeNextId)
                        .userName("普通用户2 - 测试")
                        .password("222")
                        .userRole(User.UserRole.USER)
                        .build()
        ));
    }

    /**
     * 初始化所有数据
     */
    @Test
    void initAllData() {
        long snowflakeNextId = IdUtil.getSnowflakeNextId();

        // === 抽奖领域 ===
        test_activity();
        test_strategy();
        test_strategyAward();
        test_award();
        test_RafflePool();
        // === 安全领域 ===
        test_user(snowflakeNextId);
        test_userRaffleConfig(snowflakeNextId);
        test_userPurchaseHistory();
    }

    /**
     * 初始化活动
     */
    @Test
    void test_activity() {
        activityJPA.save(Activity.builder()
                .activityId(10001L)
                .activityName("测试活动")
                .build()
        );
    }

    /**
     * 初始化用户购买历史
     */
    @Test
    void test_userPurchaseHistory() {
        List<UserPurchaseHistory> list = List.of(
                UserPurchaseHistory.builder()
                        .userId(111L)
                        .purchaseName("芥末味夏威夷果")
                        .purchaseCategory(UserPurchaseHistory.PurchaseCategory.FOOD)
                        .purchasePrice(99.0).purchaseCount(1L).purchaseTimes(1L).isReturn(false)
                        .build(),
                UserPurchaseHistory.builder()
                        .userId(111L)
                        .purchaseName("曲奇饼干")
                        .purchaseCategory(UserPurchaseHistory.PurchaseCategory.FOOD)
                        .purchasePrice(20.0).purchaseCount(2L).purchaseTimes(2L).isReturn(false)
                        .build(),
                UserPurchaseHistory.builder()
                        .userId(111L)
                        .purchaseName("纯牛奶")
                        .purchaseCategory(UserPurchaseHistory.PurchaseCategory.FOOD)
                        .purchasePrice(30.0).purchaseCount(3L).purchaseTimes(3L).isReturn(false)
                        .build(),
                UserPurchaseHistory.builder()
                        .userId(111L)
                        .purchaseName("冰箱")
                        .purchaseCategory(UserPurchaseHistory.PurchaseCategory.FOOD)
                        .purchasePrice(9999.0).purchaseCount(1L).purchaseTimes(1L).isReturn(false)
                        .build()
        );
        userPurchaseHistoryJpa.saveAll(list);
    }

    /**
     * 初始化用户抽奖配置
     */
    @Test
    void test_userRaffleConfig(long snowflakeNextId) {
        userRaffleConfigJpa.saveAll(List.of(
                UserRaffleConfig.builder()
                        .userId(404L)
                        .activityId(10001L)
                        .strategyId(10001L)
                        .build(),
                UserRaffleConfig.builder()
                        .userId(200L)
                        .activityId(10001L)
                        .strategyId(10001L)
                        .build(),
                UserRaffleConfig.builder()
                        .userId(snowflakeNextId)
                        .activityId(10001L)
                        .strategyId(10001L)
                        .build()
        ));
    }

    /**
     * 初始化策略
     */
    @Test
    void test_strategy() {
        strategyJpa.saveAll(List.of(
                Strategy.builder().activityId(10001L).strategyId(10001L).strategyDesc("策略 1").build(),
                Strategy.builder().activityId(10001L).strategyId(IdUtil.getSnowflakeNextId()).strategyDesc("策略 2 - 测试").build()
        ));
    }

    /**
     * 初始化策略·奖品中间表
     */
    @Test
    void test_strategyAward() {
        strategyAwardJpa.saveAll(List.of(
                StrategyAward.builder().strategyId(10001L).awardId(101L).awardCount(8000L).awardRate(74.0).awardSort(1).build(),
                StrategyAward.builder().strategyId(10001L).awardId(102L).awardCount(50L).awardRate(4.0).awardSort(2).build(),
                StrategyAward.builder().strategyId(10001L).awardId(103L).awardCount(50L).awardRate(4.0).awardSort(3).build(),
                StrategyAward.builder().strategyId(10001L).awardId(104L).awardCount(50L).awardRate(4.0).awardSort(4).build(),
                StrategyAward.builder().strategyId(10001L).awardId(105L).awardCount(50L).awardRate(4.0).awardSort(5).build(),
                StrategyAward.builder().strategyId(10001L).awardId(106L).awardCount(10L).awardRate(3.0).awardSort(6).build(),
                StrategyAward.builder().strategyId(10001L).awardId(107L).awardCount(10L).awardRate(3.0).awardSort(7).build(),
                StrategyAward.builder().strategyId(10001L).awardId(108L).awardCount(10L).awardRate(3.0).awardSort(8).build(),
                StrategyAward.builder().strategyId(10001L).awardId(109L).awardCount(1L).awardRate(1.0).awardSort(9).build()
        ));
    }

    /**
     * 初始化奖品
     */
    @Test
    void test_award() {
        awardJpa.saveAll(List.of(
                Award.builder().awardId(101L).awardTitle("随机积分").build(),
                Award.builder().awardId(102L).awardTitle("淘宝优惠券").build(),
                Award.builder().awardId(103L).awardTitle("京东优惠券").build(),
                Award.builder().awardId(104L).awardTitle("1 天 VIP").build(),
                Award.builder().awardId(105L).awardTitle("高额随机积分").build(),
                Award.builder().awardId(106L).awardTitle("付费音乐 30 天免费听").awardSubtitle("抽奖 10 次后解锁").build(),
                Award.builder().awardId(107L).awardTitle("付费电影 30 天免费看").awardSubtitle("抽奖 10 次后解锁").build(),
                Award.builder().awardId(108L).awardTitle("付费小说 30 天免费看").awardSubtitle("抽奖 10 次后解锁").build(),
                Award.builder().awardId(109L).awardTitle("iPhone 15 Pro Max").awardSubtitle("抽奖 20 次后解锁").build()
        ));
    }

    /**
     * 初始化用户
     */
    @Test
    void test_user(long snowflakeNextId) {
        userJpa.saveAll(List.of(
                User.builder()
                        .userId(404L)
                        .userName("404 用户")
                        .password("404")
                        .userRole(User.UserRole.BLACKLIST)
                        .build(),
                User.builder()
                        .userId(200L)
                        .userName("游客用户")
                        .password("user200")
                        .userRole(User.UserRole.USER)
                        .build(),
                User.builder()
                        .userId(111L)
                        .userName("普通用户1")
                        .password("111")
                        .userRole(User.UserRole.USER)
                        .build(),
                User.builder()
                        .userId(snowflakeNextId)
                        .userName("普通用户2")
                        .password("222")
                        .userRole(User.UserRole.USER)
                        .build(),
                new EasyRandom().nextObject(User.class)
        ));
    }

    /**
     * 初始化抽奖池规则
     */
    @Test
    void test_RafflePool() {
        rafflePoolJpa.saveAll(List.of(
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
