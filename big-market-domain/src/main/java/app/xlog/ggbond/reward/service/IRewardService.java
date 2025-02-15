package app.xlog.ggbond.reward.service;

import app.xlog.ggbond.reward.model.RewardTaskBO;

/**
 * 奖品发放领域 - 奖品发放服务
 */
public interface IRewardService {

    // 奖品发放领域 - 写入task记录
    long insertRewardTask(RewardTaskBO rewardTaskBO);

    // 奖品发放领域 - 发送奖品发放任务消息
    void sendRewardToMQ(RewardTaskBO rewardTaskBO);

    // 奖品发放领域 - 更新奖品发放任务状态
    void updateRewardTaskStatus(Long awardIssuanceId, boolean isIssued);

    // 奖品发放领域 - 扫描task表，补偿未发放奖品的用户
    void scanAndCompensateNotReward(Long scanAwardIssuanceTaskTime);

}
