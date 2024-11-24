package app.xlog.ggbond.domain;

import app.xlog.ggbond.gpt.GLM4FlashService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GLM4FlashTest {

    @Resource
    private GLM4FlashService glm4FlashService;

    @Test
    void test_1() throws JsonProcessingException {
        String answer = glm4FlashService.syncInvoke("""
                Map<String, Integer> map = MapBuilder.create(new HashMap<String, Integer>())
                		.put("a", 1)
                		.put("b", 2)
                		.map();
                                
                hutool 有没有List的流式构建
                """);
    }
}
