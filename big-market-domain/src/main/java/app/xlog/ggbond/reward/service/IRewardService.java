package app.xlog.ggbond.reward.service;

import app.xlog.ggbond.reward.model.RewardTaskBO;

/**
 * 返利领域 - 返利服务
 */
public interface IRewardService {

    // 写入task记录
    long insertRewardTask(RewardTaskBO rewardTaskBO);

    // 发送返利任务消息
    void sendRewardToMQ(RewardTaskBO rewardTaskBO);

    // 更新返利任务状态
    void updateRewardTaskStatus(Long awardIssuanceId, boolean isIssued);

    // 扫描task表，补偿未发放奖品的用户
    void scanAndCompensateNotReward(Long scanAwardIssuanceTaskTime);

}
