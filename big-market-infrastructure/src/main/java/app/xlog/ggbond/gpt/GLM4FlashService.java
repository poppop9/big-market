package app.xlog.ggbond.gpt;

import app.xlog.ggbond.recommend.IntelligentRecommendService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 智谱 AI 服务
 */
@Slf4j
@Service
public class GLM4FlashService implements IntelligentRecommendService {

    @Value("${glm.modelName}")
    private String modelName;

    @Resource
    private ClientV4 clientV4;
    @Resource
    private ObjectMapper objectMapper;


    // --------------------------
    // ---- 对外的推荐服务封装 -----
    // --------------------------




    /**
     * GPT调用方法 - 同步调用
     */
    public String syncInvoke(String question) {
        List<ChatMessage> chatMessages = List.of(new ChatMessage(
                ChatMessageRole.USER.value(), question
        ));

        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(ChatCompletionRequest.builder()
                .model(modelName)  // 模型
                .stream(Boolean.FALSE)  // 流式调用
                .invokeMethod(Constants.invokeMethod)  // 同步调用
                .messages(chatMessages)  // 对话内容
                .requestId("RecommendProduct-" + System.currentTimeMillis())  // 业务id
                .build()
        );
        String respContent = invokeModelApiResp.getData().getChoices().get(0)
                .getMessage()
                .getContent()
                .toString();

//        System.out.println(objectMapper.writeValueAsString(invokeModelApiResp));
        log.atInfo().log("推荐领域 - 智谱AI服务返回结果 : " + invokeModelApiResp.getData().getRequestId() + " " + respContent);
        return respContent;
    }

}
