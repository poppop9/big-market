package app.xlog.ggbond.activity.repository;

import app.xlog.ggbond.activity.model.ActivityOrderFlowBO;

/**
 * 活动领域 - 活动仓储
 */
public interface IActivityRepo {

    // 保存活动单流水
    void saveActivityOrderFlow(ActivityOrderFlowBO activityOrderFlowBO);

}
