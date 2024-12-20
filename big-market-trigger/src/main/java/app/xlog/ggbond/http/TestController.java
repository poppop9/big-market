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

}
