package app.xlog.ggbond.http;

import app.xlog.ggbond.TestService;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.service.statusFlow.AOEventCenter;
import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.persistent.po.activity.ActivityOrderType;
import app.xlog.ggbond.persistent.po.activity.ActivityRedeemCode;
import app.xlog.ggbond.persistent.po.reward.ExchangePrizes;
import app.xlog.ggbond.persistent.po.reward.RewardAccount;
import app.xlog.ggbond.persistent.po.security.User;
import app.xlog.ggbond.persistent.po.security.UserPurchaseHistory;
import app.xlog.ggbond.persistent.repository.jpa.*;
import app.xlog.ggbond.recommend.AIRepo;
import app.xlog.ggbond.recommend.RecommendService;
import app.xlog.ggbond.resp.BigMarketRespCode;
import app.xlog.ggbond.resp.ZakiResponse;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private ISecurityService securityService;
    @Resource
    private AOEventCenter AOEventCenter;
    @Resource
    private TestService testService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ActivityOrderJpa activityOrderJpa;
    @Resource
    private UserPurchaseHistoryJpa userPurchaseHistoryJpa;
    @Resource
    private ActivityRedeemCodeJpa activityRedeemCodeJpa;
    @Resource
    private ExchangePrizesJpa exchangePrizesJpa;
    @Resource
    private UserJpa userJpa;
    @Autowired
    private RewardAccountJpa rewardAccountJpa;
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
    public void testParamCheck(String var1,
                               String var2) {
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

    void test(String var3) {
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
        // testService.testTransaction();

/*        activityRedeemCodeJpa.save(ActivityRedeemCode.builder()
                .activityId(10001L)
                .redeemCode(IdUtil.randomUUID())
                .raffleCount(10L)
                .build());*/

        /*exchangePrizesJpa.save(ExchangePrizes.builder()
                .activityId(10001L)
                .exchangePrizesId(IdUtil.getSnowflakeNextId())
                .exchangePrizesName("10 元无门槛优惠券")
                .points(50L)
                .build()
        );
        exchangePrizesJpa.save(ExchangePrizes.builder()
                .activityId(10001L)
                .exchangePrizesId(IdUtil.getSnowflakeNextId())
                .exchangePrizesName("满 100 - 30 优惠券")
                .points(200L)
                .build()
        );
        exchangePrizesJpa.save(ExchangePrizes.builder()
                .activityId(10001L)
                .exchangePrizesId(IdUtil.getSnowflakeNextId())
                .exchangePrizesName("50 元无门槛优惠券")
                .points(500L)
                .build()
        );*/

        activityOrderJpa.updateActivityOrderProductIdByActivityOrderTypeName(
                1910937768625401856L,
                ActivityOrderType.ActivityOrderTypeName.PAID_PURCHASE
        );
    }

    /**
     * 读取excel，写入用户购买历史
     */
    @PostMapping("/v1/writePurchaseHistoryFromExcel")
    public ResponseEntity<JsonNode> writePurchaseHistoryFromExcel(MultipartFile file) {
        if (file.isEmpty()) return ZakiResponse.error("文件为空！");
        securityService.writePurchaseHistoryFromExcel(file);
        return ZakiResponse.ok("文件上传成功，数据已写入数据库");
    }

    /**
     * 清空所有活动单
     */
    @DeleteMapping("/v1/clearAllActivityOrder")
    public ResponseEntity<JsonNode> clearAllActivityOrder() {
        activityOrderJpa.deleteAll();
        return ZakiResponse.ok("清空成功");
    }

    /**
     * 将1-1000的用户购买历史复制到1001-2000用户上
     */
    @GetMapping("/v1/copyUserPurchaseHistory")
    public ResponseEntity<JsonNode> copyUserPurchaseHistory() {
        List<UserPurchaseHistory> list = userPurchaseHistoryJpa.findAll().stream().peek(item -> {
            item.setId(null);
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setUserId(item.getUserId() + 1000);
        }).toList();
        userPurchaseHistoryJpa.saveAll(list);
        return ZakiResponse.ok("请求成功");
    }

}