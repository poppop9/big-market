package app.xlog.ggbond.activity.service;

import app.xlog.ggbond.activity.model.bo.ActivityAccountBO;
import app.xlog.ggbond.activity.model.bo.ActivityBO;
import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.bo.ActivityOrderRewardTaskBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return activityRepo.findAllPendingPaymentAO(activityId, userId).stream()
                .peek(item -> item.setActivityOrderIdStr(item.getActivityOrderId().toString()))
                .toList();
    }

    /**
     * 判断 - 检查过期的待支付活动单
     */
    @Override
    public void checkExpirePendingPaymentAO(Long activityOrderId) {
        activityRepo.checkExpirePendingPaymentAO(activityOrderId);
    }

    /**
     * 扫描 - 扫描task表，补偿未发放有效活动单
     */
    @Override
    public void scanAndCompensateNotIssuanceEffectiveAO(Long scanIssuanceEffectiveActivityOrderTime) {
        List<ActivityOrderRewardTaskBO> activityOrderRewardTaskBOList = activityRepo.findRewardEffectiveAOTaskByIsIssuedAndCreateTimeBefore(
                false,
                LocalDateTime.now().minusSeconds(scanIssuanceEffectiveActivityOrderTime * 2),
                LocalDateTime.now().minusSeconds(scanIssuanceEffectiveActivityOrderTime)
        );
        for (ActivityOrderRewardTaskBO activityOrderRewardTaskBO : activityOrderRewardTaskBOList) {
            activityRepo.sendRewardEffectiveActivityOrderTaskToMQ(AOContext.builder()
                    .userId(activityOrderRewardTaskBO.getUserId())
                    .activityId(activityOrderRewardTaskBO.getActivityId())
                    .activityOrderBO(ActivityOrderBO.builder()
                            .activityOrderId(activityOrderRewardTaskBO.getActivityOrderId())
                            .activityOrderTypeId(activityOrderRewardTaskBO.getActivityOrderTypeId())
                            .build()
                    )
                    .raffleCount(activityOrderRewardTaskBO.getRaffleCount())
                    .build()
            );
        }
    }

    /**
     * 更新 - 更新活动单发放任务状态
     */
    @Override
    public void updateActivityOrderIssuanceTaskStatus(Long activityOrderIssuanceTaskId, boolean isIssued) {
        activityRepo.updateActivityOrderRewardTaskStatus(activityOrderIssuanceTaskId, isIssued);
    }

    /**
     * 查询 - 查询活动
     */
    @Override
    public ActivityBO findActivityByActivityId(Long activityId) {
        return activityRepo.findActivityByActivityId(activityId);
    }

    /**
     * 查询 - 查询用户的可用抽奖次数
     */
    @Override
    public Long findAvailableRaffleCount(Long userId, Long activityId) {
        ActivityAccountBO activityAccount = activityRepo.findActivityAccountByUserIdAndActivityId(userId, activityId);
        return activityAccount.getAvailableRaffleCount();
    }

}
