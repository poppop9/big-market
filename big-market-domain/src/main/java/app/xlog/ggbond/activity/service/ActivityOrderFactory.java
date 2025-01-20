package app.xlog.ggbond.activity.service;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动单工厂
 * <p>
 * - 专门生成活动单
 */
@Slf4j
@LiteflowComponent
public class ActivityOrderFactory {

    /**
     * 活动单类型路由器 - 跟据活动单类型路由到相应的判断条件去
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.SWITCH,
            value = LiteFlowMethodEnum.PROCESS_SWITCH,
            nodeId = "ActivityOrderTypeRouter",
            nodeName = "活动单类型路由器")
    public String ActivityOrderTypeRouter(NodeComponent bindCmp) {
        // todo
        return "nodeId";
    }

    /**
     * 签到领取裁判 - 判断是否满足签到领取的条件
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.BOOLEAN,
            value = LiteFlowMethodEnum.PROCESS_BOOLEAN,
            nodeId = "SignInToClaimJudge",
            nodeName = "签到领取裁判")
    public boolean signInToClaimJudge(NodeComponent bindCmp) {
        // todo
        return true;
    }

    /**
     * 免费赠送裁判
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.BOOLEAN,
            value = LiteFlowMethodEnum.PROCESS_BOOLEAN,
            nodeId = "FreeGiveawayJudge",
            nodeName = "免费赠送裁判")
    public boolean freeGiveawayJudge(NodeComponent bindCmp) {
        // todo
        return true;
    }

    /**
     * 创建成功 - 创建活动单工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "CreateActivityOrderWorkstation",
            nodeName = "创建活动单工位")
    public void createActivityOrderWorkstation(NodeComponent bindCmp) {
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

    /**
     * 创建失败 - 处理创建活动单失败工位
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "HandleFailedCreateActivityOrderWorkstation",
            nodeName = "处理创建活动单失败工位")
    public void handleFailedCreateActivityOrderWorkstation(NodeComponent bindCmp) {
    }

}
