package app.xlog.ggbond.job;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.vo.QueueItemVO;
import app.xlog.ggbond.activity.service.IActivityService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 活动领域 - 任务
 */
@Slf4j
@Component
public class ActivityJob {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private IActivityService activityService;

    /**
     * 从检查过期待支付活动单队列中取出数据，检查是否过期
     */
    @XxlJob("check_expire_pending_payment_ao_queue")
    public void checkExpirePendingPaymentAOQueue() {
        RQueue<QueueItemVO> rQueue = redissonClient.getQueue(GlobalConstant.RedisKey.CHECK_EXPIRE_PENDING_PAYMENT_AO_QUEUE);
        // 如果该元素目前的状态还是待支付状态，则将其修改为已关闭
        QueueItemVO item;
        while ((item = rQueue.poll()) != null) {
            // 处理 item
            Long activityOrderId = item.getActivityOrderId();
            activityService.checkExpirePendingPaymentAO(activityOrderId);
        }
    }

    /**
     * 扫描task表，补偿未发放有效活动单
     * todo 未测试
     */
    @XxlJob("scan_and_compensate_not_issuance_effective_ao")
    public void scanAndCompensateNotIssuanceEffectiveAO() {
        activityService.scanAndCompensateNotIssuanceEffectiveAO(
                GlobalConstant.SCAN_ISSUANCE_EFFECTIVE_ACTIVITY_ORDER_TIME
        );
    }

}
