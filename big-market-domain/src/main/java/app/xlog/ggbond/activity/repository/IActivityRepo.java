package app.xlog.ggbond.activity.repository;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeConfigBO;

import java.util.List;

/**
 * 活动领域 - 活动仓储
 */
public interface IActivityRepo {

    // 插入 - 插入活动单流水
    void saveActivityOrder(ActivityOrderBO activityOrderBO);

    // 查询 - 根据活动id查询活动单类型配置
    List<ActivityOrderTypeConfigBO> findActivityOrderTypeConfigByActivityId(Long activityId);
}
