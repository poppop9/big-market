package app.xlog.ggbond.http;

import app.xlog.ggbond.recommend.IntelligentRecommendService;
import jakarta.annotation.Resource;
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
    public String bigModelAnswer() {
        String answer = intelligentRecommendService.syncInvoke("""
                Caused by: org.apache.shardingsphere.sharding.exception.metadata.DuplicateIndexException: Index 'IDXby462r777g4s8bki2waeghb0i' already exists.
                """);
        return answer;
    }

}
