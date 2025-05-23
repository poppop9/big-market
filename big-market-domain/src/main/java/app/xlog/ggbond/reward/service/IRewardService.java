package app.xlog.ggbond.reward.service;

import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.reward.model.PointsLogBO;
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

    // 插入积分流水
    PointsLogBO insetPointsLog(Long activityId, Long userId, int points, boolean isIssued);

    // 发送积分奖励消息
    void publishPointsRewardMessage(MQMessage<PointsLogBO> build);

    // 充值返利账户积分
    void rechargeRewardAccountPoints(Long activityId, Long userId, Long points);

    // 更新积分流水是否发放
    void updatePointsLogIsIssued(Long pointsLogId, boolean b);

    // 初始化返利账户
    void initRewardAccount(Long userId, long activityId);

    // 查询用户积分
    Long findUserRewardAccountPoints(Long userId, Long activityId);

}
