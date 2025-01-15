package app.xlog.ggbond.gpt;

import app.xlog.ggbond.recommend.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 免费 gpt 服务
 * <p>
 * - https://github.com/chatanywhere/GPT_API_free
 */
@Service
public class FreeChatGPTService implements AIService {

    @Value("${freeGPT.model}")
    private String model;
    @Value("${freeGPT.apiKey}")
    private String apiKey;

    @Resource
    private OkHttpClient okHttpClient;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * GPT调用方法 - 同步调用
     * <p>
     * - 请求限制：一天200次
     * - 余额查询：https://api.chatanywhere.tech/#/
     * - 请求路径：
     * ----- https://api.chatanywhere.tech(国内中转，延时更低)，
     * ----- https://api.chatanywhere.org (国外使用)
     */
    @SneakyThrows
    public String syncInvoke(String roleDesc, String question) {
        Request request = new Request.Builder()
                .url("https://api.chatanywhere.tech/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(objectMapper.writeValueAsString(objectMapper.createObjectNode()
                                .put("model", model)
                                .putPOJO("messages", List.of(
                                        objectMapper.createObjectNode()
                                                .put("role", "system")  // 系统设定 : 用来给模型设定上下文规则
                                                .put("content", roleDesc),
                                        objectMapper.createObjectNode()
                                                .put("role", "user")  // 用户提的问题
                                                .put("content", question)
                                ))),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readTree(response.body().string())
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
