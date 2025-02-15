package app.xlog.ggbond.reward.repository;

import app.xlog.ggbond.reward.model.RewardTaskBO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 奖品发放领域 - 奖品发放仓储
 */
public interface IRewardRepo {

    // 新增 - 写入task记录
    long insertRewardTask(RewardTaskBO rewardTaskBO);

    // 新增 - 发送奖品发放任务
    void sendRewardToMQ(RewardTaskBO rewardTaskBO);

    // 更新 - 更新奖品发放任务状态
    void updateRewardTaskStatus(Long awardIssuanceId, boolean isIssued);

    // 查询 - 查询所有指定时间内，未发放奖品的记录
    List<RewardTaskBO> findRewardTaskByIsIssuedAndCreateTimeBefore(boolean isIssued, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
