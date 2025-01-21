package app.xlog.ggbond.activity.service;

/**
 * 活动领域 - 活动服务接口
 */
public interface IActivityService {

    // 新增 - 初始化活动账户
    void initActivityAccount(Long userId, long activityId);

}
