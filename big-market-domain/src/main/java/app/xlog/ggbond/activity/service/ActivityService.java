package app.xlog.ggbond.activity.service;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动领域 - 活动服务
 */
@Service
public class ActivityService implements IActivityService {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * 新增 - 初始化活动账户
     */
    @Override
    public void initActivityAccount(Long userId, long activityId) {
        activityRepo.initActivityAccount(userId, activityId);
    }

    /**
     * 查询 - 查询所有待支付的活动单
     */
    @Override
    public List<ActivityOrderBO> findAllPendingPaymentAO(Long activityId, Long userId) {
        return activityRepo.findAllPendingPaymentAO(activityId,userId);
    }

    /**
     * 判断 - 检查过期的待支付活动单
     */
    @Override
    public void checkExpirePendingPaymentAO(Long activityOrderId) {
        activityRepo.checkExpirePendingPaymentAO(activityOrderId);
    }

}
