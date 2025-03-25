package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
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
 * 待支付状态 -> 已关闭状态流水线
 */
@Slf4j
@LiteflowComponent
public class PendPayToClosedPipeline {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * perform - 待支付状态转为关闭状态工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "PendingPaymentToClosedWorker",
            nodeName = "待支付状态转为关闭状态工位")
    public void pendingPaymentToClosedWorker(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        activityRepo.updateActivityOrderStatus(
                context.getActivityOrderBO().getActivityOrderId(),
                ActivityOrderBO.ActivityOrderStatus.CLOSED
        );
    }

}
