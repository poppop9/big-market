package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeConfigBO;
import app.xlog.ggbond.activity.model.po.ActivityRedeemCodeBO;
import app.xlog.ggbond.activity.model.vo.QueueItemVO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.persistent.po.activity.*;
import app.xlog.ggbond.persistent.repository.jpa.ActivityAccountJpa;
import app.xlog.ggbond.persistent.repository.jpa.ActivityOrderJpa;
import app.xlog.ggbond.persistent.repository.jpa.ActivityOrderTypeConfigJpa;
import app.xlog.ggbond.persistent.repository.jpa.ActivityRedeemCodeJpa;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 活动领域 - 活动仓储实现类
 */
@Repository
public class ActivityRepository implements IActivityRepo {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ActivityOrderJpa activityOrderJPA;
    @Resource
    private ActivityOrderTypeConfigJpa activityOrderTypeConfigJpa;
    @Resource
    private ActivityAccountJpa activityAccountJpa;
    @Resource
    private ActivityRedeemCodeJpa activityRedeemCodeJpa;

    /**
     * 插入 - 插入活动单流水
     */
    @Override
    public ActivityOrderBO saveActivityOrder(ActivityOrderBO activityOrderBO) {
        ActivityOrder activityOrder = activityOrderJPA.save(
                BeanUtil.copyProperties(activityOrderBO, ActivityOrder.class)
        );
        return BeanUtil.copyProperties(activityOrder, ActivityOrderBO.class);
    }

    /**
     * 查询 - 根据活动id查询活动单类型配置
     */
    @Override
    public List<ActivityOrderTypeConfigBO> findActivityOrderTypeConfigByActivityId(Long activityId) {
        List<ActivityOrderTypeConfig> byActivityId = activityOrderTypeConfigJpa.findByActivityId(activityId);
        return BeanUtil.copyToList(byActivityId, ActivityOrderTypeConfigBO.class);
    }

    /**
     * 更新 - 增加可用抽奖次数
     */
    @Override
    public void increaseAvailableRaffleTime(Long userId, Long activityId, Long raffleCount) {
        activityAccountJpa.updateAvailableRaffleCountByUserIdAndActivityId(raffleCount, userId, activityId);
    }

    /**
     * 新增 - 初始化活动账户
     */
    @Override
    public void initActivityAccount(Long userId, long activityId) {
        if (!activityAccountJpa.existsByUserIdAndActivityId(userId, activityId)) {
            activityAccountJpa.save(new ActivityAccount(
                    userId, activityId, 0L, 100d
            ));
        }
    }

    /**
     * 判断 - 判断今天是否已签到领取AO
     */
    @Override
    public boolean existSignInToClaimAOToday(Long userId, Long activityId) {
        return activityOrderJPA.existsByUserIdAndActivityIdAndActivityOrderTypeNameAndActivityOrderStatusAndCreateTimeBetween(
                userId,
                activityId,
                ActivityOrderType.ActivityOrderTypeName.SIGN_IN_TO_CLAIM,
                ActivityOrder.ActivityOrderStatus.EFFECTIVE,
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        );
    }

    /**
     * 更新 - 更新活动单状态
     */
    @Override
    public void updateActivityOrderStatus(Long activityOrderId, ActivityOrderBO.ActivityOrderStatus activityOrderStatus) {
        ActivityOrder.ActivityOrderStatus activityOrderStatus1 = ActivityOrder.ActivityOrderStatus.valueOf(activityOrderStatus.name());
        activityOrderJPA.updateActivityOrderStatusByActivityOrderId(
                activityOrderStatus1, activityOrderId
        );
    }

    /**
     * 查询 - 查询所有待支付的活动单
     */
    @Override
    public List<ActivityOrderBO> findAllPendingPaymentAO(Long activityId, Long userId) {
        List<ActivityOrder> activityOrderList = activityOrderJPA.findByActivityIdAndUserIdAndActivityOrderStatusOrderByCreateTimeAsc(
                activityId,
                userId,
                ActivityOrder.ActivityOrderStatus.PENDING_PAYMENT
        );
        activityOrderList = activityOrderList.stream()
                .filter(item -> {
                    ActivityOrderType.ActivityOrderTypeName activityOrderTypeName = item.getActivityOrderTypeName();
                    return activityOrderTypeName.equals(ActivityOrderType.ActivityOrderTypeName.PAID_PURCHASE);
                })
                .collect(Collectors.toList());
        return BeanUtil.copyToList(activityOrderList, ActivityOrderBO.class);
    }

    /**
     * 新增 - 将检查过期的待支付活动单插入队列
     */
    @Override
    public void insertCheckExpirePendingPaymentAOQueue(QueueItemVO queueItemVO) {
        Duration between = Duration.between(LocalDateTime.now(), queueItemVO.getActivityOrderExpireTime());
        long delayed = between.getSeconds();

        RQueue<QueueItemVO> rQueue = redissonClient.getQueue(GlobalConstant.RedisKey.CHECK_EXPIRE_PENDING_PAYMENT_AO_QUEUE);
        RDelayedQueue<QueueItemVO> rDelayedQueue = redissonClient.getDelayedQueue(rQueue);
        rDelayedQueue.offer(
                queueItemVO,
                delayed,
                TimeUnit.SECONDS
        );
    }

    /**
     * 判断 - 检查过期的待支付活动单
     */
    @Override
    public void checkExpirePendingPaymentAO(Long activityOrderId) {
        ActivityOrder activityOrder = activityOrderJPA.findByActivityOrderId(activityOrderId);
        if (activityOrder.getActivityOrderStatus().equals(ActivityOrder.ActivityOrderStatus.PENDING_PAYMENT)) {
            // 如果该活动单还是待支付状态，则将其置为已关闭
            activityOrderJPA.updateActivityOrderStatusByActivityOrderId(
                    ActivityOrder.ActivityOrderStatus.CLOSED, activityOrderId
            );
        }
    }

    /**
     * 判断 - 判断兑换码是否有效
     */
    @Override
    public boolean existRedeemCode(Long activityId, String redeemCode) {
        return activityRedeemCodeJpa.existsByActivityIdAndIsUsedAndRedeemCode(
                activityId, false, redeemCode
        );
    }

    /**
     * 查询 - 根据兑换码查询兑换码信息
     */
    @Override
    public ActivityRedeemCodeBO findActivityRedeemCodeByRedeemCode(String redeemCode) {
        ActivityRedeemCode activityRedeemCode = activityRedeemCodeJpa.findByRedeemCode(redeemCode);
        return BeanUtil.copyProperties(activityRedeemCode, ActivityRedeemCodeBO.class);
    }

    /**
     * 更新 - 更新兑换码使用状态
     */
    @Override
    public void updateActivityRedeemCodeIsUsed(Long userId, String redeemCode) {
        activityRedeemCodeJpa.updateUserIdAndIsUsedByRedeemCode(userId, true, redeemCode);
    }

    /**
     * 更新 - 更新活动单状态和活动单类型id
     */
    @Override
    public void updateActivityOrderStatusAndAOTypeId(Long activityOrderId, ActivityOrderBO.ActivityOrderStatus activityOrderStatus, Long activityOrderTypeId) {
        activityOrderJPA.updateActivityOrderStatusAndActivityOrderTypeIdByActivityOrderId(
                ActivityOrder.ActivityOrderStatus.valueOf(activityOrderStatus.name()),
                activityOrderTypeId,
                activityOrderId
        );
    }

    /**
     * 更新 - 更新活动单状态、活动单类型id和总抽奖次数
     */
    @Override
    public void updateActivityOrderStatusAndAOTypeIdAndTotalRaffleCount(Long activityOrderId, ActivityOrderBO.ActivityOrderStatus activityOrderStatus, Long activityOrderTypeId, Long raffleCount) {
        activityOrderJPA.updateActivityOrderStatusAndActivityOrderTypeIdAndTotalRaffleCountByActivityOrderId(
                ActivityOrder.ActivityOrderStatus.valueOf(activityOrderStatus.name()),
                activityOrderTypeId,
                raffleCount,
                activityOrderId
        );
    }

    /**
     * 更新 - 更新过期的活动单状态
     */
    @Override
    public void updateExpiredAOStatus(Long activityId, Long userId) {
        List<ActivityOrder> activityOrderList = activityOrderJPA.findByActivityIdAndUserIdAndActivityOrderStatus(
                activityId, userId, ActivityOrder.ActivityOrderStatus.EFFECTIVE
        );
        activityOrderList.stream()
                .filter(item -> item
                        .getActivityOrderExpireTime()
                        .isBefore(LocalDateTime.now())
                )
                .forEach(item ->
                        activityOrderJPA.updateActivityOrderStatusByActivityOrderId(
                                ActivityOrder.ActivityOrderStatus.EXPIRED,
                                item.getActivityOrderId()
                        )
                );
    }

    /**
     * 查询 - 查询有效的活动单
     */
    @Override
    public Optional<ActivityOrderBO> findEffectiveActivityOrder(Long activityId, Long userId) {
        Optional<ActivityOrder> activityOrder = activityOrderJPA.findFirstByActivityIdAndUserIdAndActivityOrderStatusOrderByCreateTimeAsc(
                activityId, userId, ActivityOrder.ActivityOrderStatus.EFFECTIVE
        );
        return activityOrder.map(item -> BeanUtil.copyProperties(item, ActivityOrderBO.class));
    }

    /**
     * 查询 - 根据活动单id查询活动单
     */
    @Override
    public ActivityOrderBO findActivityOrderByActivityOrderId(Long activityOrderId) {
        ActivityOrder activityOrder = activityOrderJPA.findByActivityOrderId(activityOrderId);
        return BeanUtil.copyProperties(activityOrder, ActivityOrderBO.class);
    }

    /**
     * 更新 - 增加活动单已使用的抽奖次数
     */
    @Override
    public Long increaseAOUsedRaffleCount(Long activityOrderId) {
        activityOrderJPA.updateUsedRaffleCountByActivityOrderId(activityOrderId);
        return activityOrderJPA.findByActivityOrderId(activityOrderId).getUsedRaffleCount();
    }

}
