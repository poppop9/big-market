package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.model.vo.QueueItemVO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 初始状态 -> 待支付状态流水线
 */
@Slf4j
@LiteflowComponent
public class InitialToPendPayPipeline {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * perform - 初始状态转为待支付状态工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "InitialToPendingPaymentWorker",
            nodeName = "初始状态转为待支付状态工位")
    public void createPendingPaymentWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        ActivityOrderBO activityOrderBO = activityRepo.saveActivityOrder(ActivityOrderBO.builder()
                .userId(context.getUserId())
                .activityId(context.getActivityId())
                .activityOrderTypeName(context.getActivityOrderType().getActivityOrderTypeName())
                .activityOrderEffectiveTime(LocalDateTime.now())
                .activityOrderExpireTime(LocalDateTime.now().plusSeconds(GlobalConstant.ACTIVITY_ORDER_EXPIRE_TIME))
                .activityOrderStatus(ActivityOrderBO.ActivityOrderStatus.PENDING_PAYMENT)
                .build()
        );
        context.setActivityOrderBO(activityOrderBO);
    }

    /**
     * perform - 检查过期的待支付活动单工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "CheckExpirePendingPaymentAOWorker",
            nodeName = "检查过期的待支付活动单工位")
    public void checkPendingPaymentAOExpireWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        // 将该活动单加入redis延迟队列，之后在指定时间后取出，判断是否关闭该活动单
        activityRepo.insertCheckExpirePendingPaymentAOQueue(QueueItemVO.builder()
                .activityOrderId(context.getActivityOrderBO().getActivityOrderId())
                .activityOrderExpireTime(context.getActivityOrderBO().getActivityOrderExpireTime())
                .build()
        );
    }

}
