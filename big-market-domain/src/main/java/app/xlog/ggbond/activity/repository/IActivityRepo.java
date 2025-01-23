package app.xlog.ggbond.activity.repository;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeConfigBO;

import java.util.List;

/**
 * 活动领域 - 活动仓储
 */
public interface IActivityRepo {

    // 插入 - 插入活动单流水
    ActivityOrderBO saveActivityOrder(ActivityOrderBO activityOrderBO);

    // 查询 - 根据活动id查询活动单类型配置
    List<ActivityOrderTypeConfigBO> findActivityOrderTypeConfigByActivityId(Long activityId);

    // 更新 - 增加可用抽奖次数
    void increaseAvailableRaffleTime(Long userId, Long activityId, Long raffleCount);

    // 新增 - 初始化活动账户
    void initActivityAccount(Long userId, long activityId);

    // 判断 - 判断今天是否已签到领取AO
    boolean existSignInToClaimAOToday(Long userId, Long activityId);

}
