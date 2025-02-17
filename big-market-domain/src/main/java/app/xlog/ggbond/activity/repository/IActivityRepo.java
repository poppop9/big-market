package app.xlog.ggbond.activity.repository;

import app.xlog.ggbond.activity.model.bo.*;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.model.vo.QueueItemVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // 更新 - 更新活动单状态
    void updateActivityOrderStatus(Long activityOrderId, ActivityOrderBO.ActivityOrderStatus activityOrderStatus);

    // 查询 - 查询所有待支付的活动单
    List<ActivityOrderBO> findAllPendingPaymentAO(Long activityId, Long userId);

    // 新增 - 将检查过期的待支付活动单插入队列
    void insertCheckExpirePendingPaymentAOQueue(QueueItemVO queueItemVO);

    // 判断 - 检查过期的待支付活动单
    void checkExpirePendingPaymentAO(Long activityOrderId);

    // 判断 - 判断兑换码是否有效
    boolean existRedeemCode(Long activityId, String redeemCode);

    // 查询 - 根据兑换码查询兑换码信息
    ActivityRedeemCodeBO findActivityRedeemCodeByRedeemCode(String redeemCode);

    // 查询 - 查询活动单商品
    ActivityOrderProductBO findAOProductByAOProductId(Long aoProductId);

    // 更新 - 更新兑换码使用状态
    void updateActivityRedeemCodeIsUsed(Long userId, String redeemCode);

    // 更新 - 更新活动单状态和活动单类型id
    void updateActivityOrderStatusAndAOTypeId(Long activityOrderId, ActivityOrderBO.ActivityOrderStatus activityOrderStatus, Long activityOrderTypeId);

    // 更新 - 更新活动单状态、活动单类型id和总抽奖次数
    void updateActivityOrderStatusAndAOTypeIdAndTotalRaffleCount(Long activityOrderId, ActivityOrderBO.ActivityOrderStatus activityOrderStatus, Long activityOrderTypeId, Long raffleCount);

    // 更新 - 更新过期的活动单状态
    void updateExpiredAOStatus(Long activityId, Long userId);

    // 查询 - 查询有效的活动单
    Optional<ActivityOrderBO> findEffectiveActivityOrder(Long activityId, Long userId);

    // 查询 - 根据活动单id查询活动单
    ActivityOrderBO findActivityOrderByActivityOrderId(Long activityOrderId);

    // 更新 - 增加活动单已使用的抽奖次数
    Long increaseAOUsedRaffleCount(Long activityOrderId);

    // 更新 - 扣减用户可用抽奖次数
    void decreaseUserAvailableRaffleCount(Long activityId, Long userId);

    // 判断 - 判断用户是否在消费活动单
    boolean isUserInConsumeAO(Long userId);

    // 修改 - 在BitSet中给用户加锁
    void lockUserInConsumeAO(Long userId);

    // 修改 - 在BitSet中给用户解锁
    void unLockUserInBitSet(Long userId);

    // 查询 - 查询活动账户
    ActivityAccountBO findActivityAccountByUserIdAndActivityId(Long userId, Long activityId);

    // 更新 - 更新活动账户余额
    void updateActivityAccountBalanceByUserIdAndActivityId(double balance, Long userId, Long activityId);

    // 新增 - 发送发放有效活动单任务到MQ
    void sendRewardEffectiveActivityOrderTaskToMQ(AOContext c);

    // 新增 - 插入活动单发放任务
    Long insertActivityOrderRewardTask(ActivityOrderRewardTaskBO build);

    // 更新 - 更新活动单发放任务状态
    void updateActivityOrderRewardTaskStatus(Long activityOrderIssuanceTaskId, boolean isIssued);

    // 查询 - 查询未发放有效活动单的任务
    List<ActivityOrderRewardTaskBO> findRewardEffectiveAOTaskByIsIssuedAndCreateTimeBefore(boolean b, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    // 更新 - 更新活动单过期时间
    void updateAOExpireTime(Long activityOrderId, LocalDateTime activityOrderExpireTime);
}
