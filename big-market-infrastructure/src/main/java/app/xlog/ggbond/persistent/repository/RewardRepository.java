package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.persistent.po.reward.*;
import app.xlog.ggbond.persistent.repository.jpa.*;
import app.xlog.ggbond.reward.model.*;
import app.xlog.ggbond.reward.repository.IRewardRepo;
import app.xlog.ggbond.mq.MQEventCenter;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 返利领域 - 返利仓储实现类
 */
@Repository
public class RewardRepository implements IRewardRepo {

    @Resource
    private MQEventCenter mqEventCenter;
    @Resource
    private RewardTaskJpa rewardTaskJpa;
    @Resource
    private PointsLogJpa pointsLogJpa;
    @Resource
    private RewardAccountJpa rewardAccountJpa;
    @Autowired
    private ExchangePrizesJpa exchangePrizesJpa;
    @Autowired
    private ExchangePrizesLogJpa exchangePrizesLogJpa;

    /**
     * 写入task记录
     */
    @Override
    public long insertRewardTask(RewardTaskBO rewardTaskBO) {
        RewardTask rewardTask = rewardTaskJpa.save(RewardTask.builder()
                .userId(rewardTaskBO.getUserId())
                .userRaffleHistoryId(rewardTaskBO.getUserRaffleHistoryId())
                .isIssued(false)
                .build()
        );
        return rewardTask.getRewardId();
    }

    /**
     * 发送返利任务
     */
    @Override
    public void sendRewardToMQ(RewardTaskBO rewardTaskBO) {
        mqEventCenter.sendMessage(GlobalConstant.KafkaConstant.REWARD_TASK,
                MQMessage.<RewardTaskBO>builder()
                        .data(rewardTaskBO)
                        .build()
        );
    }

    /**
     * 更新返利任务状态
     */
    @Override
    public void updateRewardTaskStatus(Long awardIssuanceId, boolean isIssued) {
        rewardTaskJpa.updateIsIssuedByAwardIssuanceId(isIssued, awardIssuanceId);
    }

    /**
     * 查询所有指定时间内，未发放奖品的记录
     */
    @Override
    public List<RewardTaskBO> findRewardTaskByIsIssuedAndCreateTimeBefore(boolean isIssued, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<RewardTask> rewardTaskList = rewardTaskJpa.findByIsIssuedAndCreateTimeBetween(
                isIssued,
                startDateTime,
                endDateTime
        );
        return BeanUtil.copyToList(rewardTaskList, RewardTaskBO.class);
    }

    /**
     * 插入积分流水
     */
    @Override
    public PointsLogBO insertPointsLog(Long activityId, Long userId, int points, boolean isIssued) {
        PointsLog pointsLog = pointsLogJpa.save(PointsLog.builder()
                .activityId(activityId)
                .userId(userId)
                .points((long) points)
                .isIssued(isIssued)
                .build());
        return BeanUtil.copyProperties(pointsLog, PointsLogBO.class);
    }

    /**
     * 发布积分返利的消息
     */
    @Override
    public void publishPointsRewardMessage(MQMessage<PointsLogBO> build) {
        mqEventCenter.sendMessage(GlobalConstant.KafkaConstant.POINTS_REWARD, build);
    }

    /**
     * 充值返利账户积分
     */
    @Override
    public void rechargeRewardAccountPoints(Long activityId, Long userId, Long points) {
        rewardAccountJpa.rechargeRewardAccountPoints(points, activityId, userId);
    }

    /**
     * 更新积分流水是否发放
     */
    @Override
    public void updatePointsLogIsIssued(Long pointsLogId, boolean isIssued) {
        pointsLogJpa.updateIsIssuedByPointsLogId(isIssued, pointsLogId);
    }

    /**
     * 初始化返利账户
     */
    @Override
    public void initRewardAccount(Long userId, long activityId) {
        if (!rewardAccountJpa.existsByActivityIdAndUserId(activityId, userId)) {
            rewardAccountJpa.save(RewardAccount.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .build());
        }
    }

    /**
     * 查询返利账户
     */
    @Override
    public RewardAccountBO findRewardAccountByUserIdAndActivityId(Long userId, Long activityId) {
        RewardAccount rewardAccount = rewardAccountJpa.findByUserIdAndActivityId(userId, activityId);
        return BeanUtil.copyProperties(rewardAccount, RewardAccountBO.class);
    }

    /**
     * 查询兑换奖品
     */
    @Override
    public List<ExchangePrizesBO> findExchangePrizes(Long activityId) {
        List<ExchangePrizes> byActivityId = exchangePrizesJpa.findByActivityId(activityId);
        return BeanUtil.copyToList(byActivityId, ExchangePrizesBO.class);
    }

    /**
     * 更新返利账户
     */
    @Override
    public void updateRewardAccount(RewardAccountBO rewardAccountBO) {
        rewardAccountJpa.updatePointsByActivityIdAndUserId(
                rewardAccountBO.getPoints(),
                rewardAccountBO.getActivityId(),
                rewardAccountBO.getUserId()
        );
    }

    /**
     * 写入流水表
     */
    @Override
    public void saveExchangePrizesLog(ExchangePrizesLogBO build) {
        exchangePrizesLogJpa.save(BeanUtil.copyProperties(build, ExchangePrizesLog.class));
    }

    /**
     * 查询兑换奖品历史
     */
    @Override
    public List<ExchangePrizesLogBO> findExchangePrizesLogList(Long activityId, Long userId) {
        List<ExchangePrizesLog> byActivityIdAndUserId = exchangePrizesLogJpa.findByActivityIdAndUserId(activityId, userId);
        return BeanUtil.copyToList(byActivityIdAndUserId, ExchangePrizesLogBO.class).stream()
                .peek(item -> {
                    exchangePrizesJpa.findByActivityId(activityId).stream()
                            .filter(child -> item.getExchangePrizesId().equals(child.getExchangePrizesId()))
                            .findFirst()
                            .ifPresent(item2 -> {
                                item.setExchangePrizesName(item2.getExchangePrizesName());
                                item.setCreateTime(item2.getCreateTime());
                            });
                })
                .toList();
    }

}
