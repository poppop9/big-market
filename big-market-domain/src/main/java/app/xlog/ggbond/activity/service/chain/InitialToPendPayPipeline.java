package app.xlog.ggbond.activity.service.chain;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.model.vo.QueueItemVO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.raffle.model.bo.UserBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.resp.BigMarketRespCode;
import cn.dev33.satoken.session.SaSession;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 初始状态 -> 待支付状态流水线
 */
@Slf4j
@LiteflowComponent
public class InitialToPendPayPipeline {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * 充值资格验证过滤器 - 判断登录后的初始化账户是否完成
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "RechargeQualificationFilter",
            nodeName = "充值资格验证过滤器")
    public void raffleQualificationFilter(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);
        SaSession saSession = context.getSaSession();

        CompletableFuture<Boolean> doLoginCompletableFuture = (CompletableFuture<Boolean>) saSession.get("doLoginCompletableFuture");
        if (doLoginCompletableFuture.get()) {
            log.atDebug().log("抽奖领域 - " + context.getUserId() + " 抽奖资格验证过滤器放行");
        } else {
            throw new BigMarketException(BigMarketRespCode.RAFFLE_CONFIG_ARMORY_ERROR);
        }
    }

    /**
     * perform - 初始状态转为待支付状态工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "InitialToPendingPaymentWorker",
            nodeName = "初始状态转为待支付状态工位")
    public void initialToPendingPaymentWorker(NodeComponent bindCmp) {
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
