package app.xlog.ggbond.activity.service.chain;

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

/**
 * 活动单最终生成的流水线
 */
@Slf4j
@LiteflowComponent
public class AOPerformPipeline {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * 创建成功 - 创建活动单工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "CreateActivityOrderWorkstation",
            nodeName = "创建活动单工位")
    public void createActivityOrderWorkstation(NodeComponent bindCmp) {
        AOContext context = bindCmp.getContextBean(AOContext.class);

        // 跟据上下文中的activityOrderTypeName，raffleCount创建活动单
        activityRepo.saveActivityOrder(ActivityOrderBO.builder()
                .userId(context.getUserId())
                .activityId(context.getActivityId())
                .activityOrderTypeId(context.getActivityOrderType().getActivityOrderTypeId())

                .build()
        );

    }

    /**
     * 创建成功 - 增加可用抽奖次数工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "IncreaseAvailableRaffleTimeWorkstation",
            nodeName = "增加可用抽奖次数工位")
    public void increaseAvailableRaffleTimeWorkstation(NodeComponent bindCmp) {
    }

}
