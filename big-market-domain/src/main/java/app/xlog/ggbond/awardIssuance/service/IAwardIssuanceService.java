package app.xlog.ggbond.awardIssuance.service;

import app.xlog.ggbond.awardIssuance.model.AwardIssuanceTaskBO;

/**
 * 奖品发放领域 - 奖品发放服务
 */
public interface IAwardIssuanceService {

    // 奖品发放领域 - 写入task记录
    long insertAwardIssuanceTask(AwardIssuanceTaskBO awardIssuanceTaskBO);

    // 奖品发放领域 - 发送奖品发放任务消息
    void sendAwardIssuanceToMQ(AwardIssuanceTaskBO awardIssuanceTaskBO);

    // 奖品发放领域 - 更新奖品发放任务状态
    void updateAwardIssuanceTaskStatus(Long awardIssuanceId, boolean isIssued);

}
