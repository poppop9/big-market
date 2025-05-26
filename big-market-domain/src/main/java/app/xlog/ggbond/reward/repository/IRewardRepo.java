package app.xlog.ggbond.reward.repository;

import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.reward.model.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 返利领域 - 返利仓储
 */
public interface IRewardRepo {

    // 新增 - 写入task记录
    long insertRewardTask(RewardTaskBO rewardTaskBO);

    // 新增 - 发送返利任务
    void sendRewardToMQ(RewardTaskBO rewardTaskBO);

    // 更新 - 更新返利任务状态
    void updateRewardTaskStatus(Long awardIssuanceId, boolean isIssued);

    // 查询 - 查询所有指定时间内，未发放奖品的记录
    List<RewardTaskBO> findRewardTaskByIsIssuedAndCreateTimeBefore(boolean isIssued, LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 新增 - 插入积分流水
    PointsLogBO insertPointsLog(Long activityId, Long userId, int points, boolean isIssued);

    // 发布积分返利的消息
    void publishPointsRewardMessage(MQMessage<PointsLogBO> build);

    // 充值返利账户积分
    void rechargeRewardAccountPoints(Long activityId, Long userId, Long points);

    // 更新积分流水是否发放
    void updatePointsLogIsIssued(Long pointsLogId, boolean isIssued);

    // 初始化返利账户
    void initRewardAccount(Long userId, long activityId);

    // 查询返利账户
    RewardAccountBO findRewardAccountByUserIdAndActivityId(Long userId, Long activityId);

    // 查询兑换奖品
    List<ExchangePrizesBO> findExchangePrizes(Long activityId);

    // 更新返利账户
    void updateRewardAccount(RewardAccountBO rewardAccountBO);

    // 写入流水表
    void saveExchangePrizesLog(ExchangePrizesLogBO build);

    // 查询兑换奖品历史
    List<ExchangePrizesLogBO> findExchangePrizesLogList(Long activityId, Long userId);
}
