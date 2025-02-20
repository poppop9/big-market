package app.xlog.ggbond.http;

import app.xlog.ggbond.TestService;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.service.statusFlow.AOEventCenter;
import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.recommend.AIRepo;
import app.xlog.ggbond.recommend.RecommendService;
import app.xlog.ggbond.resp.BigMarketRespCode;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 测试接口
 */
@Validated
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private AIRepo aiRepo;
    @Resource
    private RecommendService recommendService;
    @Resource
    @Lazy
    private TestController testController;
    @Resource
    private AOEventCenter AOEventCenter;
    @Resource
    private TestService testService;
    @Resource
    private RedissonClient redissonClient;
    // @Resource
    // private ConfigService configService;
    // @Resource
    // private NamingService namingService;

    /**
     * 测试 nacos
     */
    @SneakyThrows
    @GetMapping("/v1/testNacos")
    public void testNacos() {
        // namingService.registerInstance(
        //         "big-market-app", "localhost", 8090
        // );
        // configService.getConfig(String dataId, String group, long timeoutMs)
    }

    /**
     * 推荐领域 - 大模型回答
     */
    @RequestMapping("/v1/bigModelAnswer")
    public String bigModelAnswer() {
        String answer = aiRepo.syncInvoke(
                "你是一个推荐系统，根据用户的购买历史推荐最有可能的产品组合。",
                """
                        Caused by: org.apache.shardingsphere.sharding.exception.metadata.DuplicateIndexException: Index 'IDXby462r777g4s8bki2waeghb0i' already exists.
                        """
        );
        return answer;
    }

    /**
     * 推荐领域 - 测试生成 GPT 提示词
     */
    @GetMapping("/v1/testGenerateGptQuestion")
    public void testGenerateGptQuestion() {
/*        String question = recommendService.generateGptQuestionByUserPurchaseHistory(List.of(
                UserPurchaseHistoryBO.builder()
                        .userId(200L)
                        .purchaseName("芥末味夏威夷果")
                        .purchaseCategory(UserPurchaseHistoryBO.PurchaseCategory.FOOD)
                        .purchasePrice(99.0).purchaseCount(1L).purchaseTimes(1L).isReturn(false)
                        .build(),
                UserPurchaseHistoryBO.builder()
                        .userId(200L)
                        .purchaseName("曲奇饼干")
                        .purchaseCategory(UserPurchaseHistoryBO.PurchaseCategory.FOOD)
                        .purchasePrice(20.0).purchaseCount(2L).purchaseTimes(2L).isReturn(false)
                        .build(),
                UserPurchaseHistoryBO.builder()
                        .userId(200L)
                        .purchaseName("纯牛奶")
                        .purchaseCategory(UserPurchaseHistoryBO.PurchaseCategory.FOOD)
                        .purchasePrice(30.0).purchaseCount(3L).purchaseTimes(3L).isReturn(false)
                        .build(),
                UserPurchaseHistoryBO.builder()
                        .userId(200L)
                        .purchaseName("冰箱")
                        .purchaseCategory(UserPurchaseHistoryBO.PurchaseCategory.FOOD)
                        .purchasePrice(9999.0).purchaseCount(1L).purchaseTimes(1L).isReturn(false)
                        .build()
        ));

        String answer = aiRepo.syncInvoke(
                "你是一个推荐系统，根据用户的购买历史推荐最能吸引该用户的商品。",
                question
        );
        System.out.println(answer);*/
    }

    /**
     * 安全领域 - 判断 token 过期
     */
    @GetMapping("/v1/isTokenExpired")
    public void isTokenExpired(String var1) {
        System.out.println("Account-Session 会话超时时间 : " + StpUtil.getSessionTimeout());
        System.out.println("当前用户 : " + StpUtil.getLoginIdDefaultNull());
        System.out.println("token值 : " + StpUtil.getTokenValue());
        System.out.println("token冻结时间 : " + StpUtil.getTokenActiveTimeout());
        System.out.println("token超时时间 : " + StpUtil.getTokenTimeout());

        List<String> strings = StpUtil.searchTokenValue("", 0, -1, true);
        strings.forEach(System.out::println);

/*        StpUtil.logout();
        StpUtil.getSession().logout();*/

/*        for (String token : StpUtil.searchTokenValue("", 0, 1000, true)) {
            if (StpUtil.getTokenActiveTimeout() == -2) {
                // Token 已经过期
                System.out.println("Token 已过期: " + token);
            }
        }*/
    }

    /**
     * 活动领域 - 测试状态机
     */
    @GetMapping("/v1/testStateMachine")
    public void testStateMachine() {
        AOEventCenter.test();
    }

    /**
     * 活动领域 - 测试创建活动单
     */
    @GetMapping("/v1/testCreateActivityOrder")
    public void testCreateActivityOrder() {
        AOEventCenter.publishInitialToPendingPaymentEvent(AOContext.builder()
                .userId(404L)
                .build()
        );
    }

    /**
     * Mess - 测试参数校验
     */
    @GetMapping("/v1/testParamCheck")
    public void testParamCheck(@NotBlank(message = "var1 不能是无效文本") String var1,
                               @NotNull(message = "var2 不能为 null") String var2) {
        throw new BigMarketException(
                BigMarketRespCode.PARAMETER_VERIFICATION_FAILED,
                "lalala"
        );

/*        System.out.println("var1: " + var1);
        System.out.println("var2: " + var2);
        testController.test("");*/
    }

    /**
     * Mess - 测试参数校验，内部方法
     */
    @GetMapping("/v2/testParamCheck")
    public void testParamCheck() {
        String s = null;
        s.contains("2");
        testService.test("", null);
        // testController.test("");
    }

    void test(@NotBlank(message = "var3 不能是无效文本") String var3) {
        System.out.println("var3: " + var3);
    }

    /**
     * Mess - 测试时间戳
     */
    @GetMapping("/v1/testTimestamp")
    public void testTimestamp() {
        long epochSecond = LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Shanghai"))
                .toEpochSecond();
        System.out.println(epochSecond);
        System.out.println(LocalDateTime.of(1970, 1, 1, 0, 0, 0).atZone(ZoneId.of("UTC")).toEpochSecond());
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of("Asia/Shanghai")));
        System.out.println(Double.valueOf(epochSecond));
    }

    /**
     * Mess - 测试分布式锁
     */
    @GetMapping("/v1/testDistributedLock")
    public void testDistributedLock() {
        RLock rlock = redissonClient.getLock("testDistributedLock");
    }

    /**
     * Mess - 测试事务
     */
    @GetMapping("/v1/testTransaction")
    public void testTransaction() {
        testService.testTransaction();
    }

}
