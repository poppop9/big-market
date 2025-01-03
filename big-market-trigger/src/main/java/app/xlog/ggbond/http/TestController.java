package app.xlog.ggbond.http;

import app.xlog.ggbond.TestService;
import app.xlog.ggbond.activity.ActivityOrderEventCenter;
import app.xlog.ggbond.activity.model.ActivityOrderContext;
import app.xlog.ggbond.activity.model.ActivityOrderFlowBO;
import app.xlog.ggbond.recommend.IntelligentRecommendService;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 测试接口
 */
@Validated
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private IntelligentRecommendService intelligentRecommendService;
    @Resource
    @Lazy
    private TestController testController;
    @Resource
    private ActivityOrderEventCenter activityOrderEventCenter;
    @Resource
    private TestService testService;

    /**
     * 推荐领域 - 大模型回答
     */
    @RequestMapping("/v1/bigModelAnswer")
    public String bigModelAnswer() throws JsonProcessingException {
        String answer = intelligentRecommendService.syncInvoke(
                "你是一个推荐系统，根据用户的购买历史推荐最有可能的产品组合。",
                """
                        Caused by: org.apache.shardingsphere.sharding.exception.metadata.DuplicateIndexException: Index 'IDXby462r777g4s8bki2waeghb0i' already exists.
                        """
        );
        return answer;
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
        activityOrderEventCenter.test();
    }

    /**
     * 活动领域 - 测试创建活动单
     */
    @GetMapping("/v1/testCreateActivityOrder")
    public void testCreateActivityOrder() {
        boolean b = activityOrderEventCenter.publishCreateActivityOrderEvent(new ActivityOrderContext()
                .setUserId(404L)
                .setActivityOrderType(ActivityOrderFlowBO.ActivityOrderType.FREE_GIVEAWAY)
        );
    }


    /**
     * Mess - 测试参数校验
     */
    @GetMapping("/v1/testParamCheck")
    public void testParamCheck(@NotBlank(message = "var1 不能是无效文本") String var1,
                               @NotNull(message = "var2 不能为 null") String var2) {
        System.out.println("var1: " + var1);
        System.out.println("var2: " + var2);
        testController.test("");
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

}
