package app.xlog.ggbond.http;

import app.xlog.ggbond.recommend.IntelligentRecommendService;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private IntelligentRecommendService intelligentRecommendService;

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
    public void isTokenExpired() {
        System.out.println(StpUtil.getLoginIdDefaultNull());   // 获取当前 token 距离被冻结还剩多少时间 (单位: 秒)
        System.out.println(StpUtil.getTokenValue());   // 获取当前 token 距离被冻结还剩多少时间 (单位: 秒)
        System.out.println(StpUtil.getTokenActiveTimeout());   // 获取当前 token 距离被冻结还剩多少时间 (单位: 秒)

        System.out.println(StpUtil.getTokenTimeout());   // 获取当前登录者的 token 剩余有效时间 (单位: 秒)
        System.out.println(StpUtil.getSessionTimeout());   // 获取当前登录者的 Account-Session 剩余有效时间 (单位: 秒)
    }

}
