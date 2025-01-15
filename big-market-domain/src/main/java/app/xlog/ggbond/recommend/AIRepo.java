package app.xlog.ggbond.recommend;

/**
 * 推荐领域 - AI 服务
 */
public interface AIRepo {

    // GPT调用方法 - 同步调用
    String syncInvoke(String roleDesc, String question);

}
