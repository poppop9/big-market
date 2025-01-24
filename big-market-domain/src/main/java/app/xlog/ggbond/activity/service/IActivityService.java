package app.xlog.ggbond.activity.service;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;

import java.util.List;

/**
 * 活动领域 - 活动服务接口
 */
public interface IActivityService {

    // 新增 - 初始化活动账户
    void initActivityAccount(Long userId, long activityId);

    // 查询 - 查询所有待支付的活动单
    List<ActivityOrderBO> findAllPendingPaymentAO(Long activityId, Long userId);

    // 判断 - 检查过期的待支付活动单
    void checkExpirePendingPaymentAO(Long activityOrderId);
}
