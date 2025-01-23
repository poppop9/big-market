package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
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
 * 活动单最终生成的流水线
 */
@Slf4j
@LiteflowComponent
public class AOPerformPipeline {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * 初始状态 -> 待支付状态 : 初始状态转为待支付状态工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "InitialToPendingPaymentWorkstation",
            nodeName = "初始状态转为待支付状态工位")
    public void createPendingPaymentWorkstation(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        ActivityOrderBO activityOrderBO = activityRepo.saveActivityOrder(ActivityOrderBO.builder()
                .userId(context.getUserId())
                .activityId(context.getActivityId())
                .activityOrderTypeName(context.getActivityOrderType().getActivityOrderTypeName())
                .activityOrderEffectiveTime(LocalDateTime.now())
                .activityOrderExpireTime(LocalDateTime.now().plusSeconds(GlobalConstant.activityOrderExpireTime))
                .activityOrderStatus(ActivityOrderBO.ActivityOrderStatus.PENDING_PAYMENT)
                .build()
        );
        context.setActivityOrderBO(activityOrderBO);
    }

    /**
     * 待支付状态 -> 有效状态 : 待支付状态转为有效状态工位
     * todo
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "PendingPaymentToEffectiveWorkstation",
            nodeName = "待支付状态转为有效状态工位")
    public void pendingPaymentToEffectiveWorkstation(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        // 跟据上下文中的activityOrderTypeName，raffleCount创建活动单
    }

    /**
     * 待支付状态 -> 有效状态 : 增加可用抽奖次数工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "IncreaseAvailableRaffleTimeWorkstation",
            nodeName = "增加可用抽奖次数工位")
    public void increaseAvailableRaffleTimeWorkstation(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        activityRepo.increaseAvailableRaffleTime(
                context.getUserId(), context.getActivityId(), context.getRaffleCount()
        );
    }

}
