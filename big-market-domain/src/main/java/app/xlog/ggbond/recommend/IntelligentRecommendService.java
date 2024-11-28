package app.xlog.ggbond.recommend;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 智能推荐领域 - 智能推荐服务
 */
public interface IntelligentRecommendService {

    // GPT调用方法 - 同步调用
    String syncInvoke(String question);

}
