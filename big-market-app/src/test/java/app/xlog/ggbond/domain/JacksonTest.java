package app.xlog.ggbond.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JacksonTest {

    @Resource
    private ObjectMapper objectMapper;

    @Test
    public void test_Jackson() throws JsonProcessingException {
//        String s = """
//                {\\"rule_common_blacklist\\": \\"-1\\"}
//                """;
        String s = """
                {"rule_common_blacklist": "-1"}
                """;
        ObjectNode jsonNodes = objectMapper.readValue(s, ObjectNode.class);
        System.out.println(jsonNodes);
    }
}
