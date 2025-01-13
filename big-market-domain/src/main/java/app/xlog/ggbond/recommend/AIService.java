package app.xlog.ggbond.recommend;

/**
 * 推荐领域 - 智能推荐服务
 */
public interface AIService {

    // GPT调用方法 - 同步调用
    String syncInvoke(String roleDesc, String question);

}
