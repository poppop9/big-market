package app.xlog.ggbond.reward.service;

import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.resp.BigMarketRespCode;
import app.xlog.ggbond.reward.model.*;
import app.xlog.ggbond.reward.repository.IRewardRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 返利领域 - 返利服务
 */
@Service
public class RewardService implements IRewardService {

    @Resource
    private IRewardRepo rewardRepo;

    /**
     * 写入task记录
     */
    @Override
    public long insertRewardTask(RewardTaskBO rewardTaskBO) {
        long awardIssuanceId = rewardRepo.insertRewardTask(rewardTaskBO);
        return awardIssuanceId;
    }

    /**
     * 发送返利任务消息
     */
    @Override
    public void sendRewardToMQ(RewardTaskBO rewardTaskBO) {
        rewardRepo.sendRewardToMQ(rewardTaskBO);
    }

    /**
     * 更新返利任务状态
     */
    @Override
    public void updateRewardTaskStatus(Long awardIssuanceId, boolean isIssued) {
        rewardRepo.updateRewardTaskStatus(awardIssuanceId, isIssued);
    }

    /**
     * 扫描task表，补偿未发放奖品的用户
     */
    @Override
    public void scanAndCompensateNotReward(Long scanAwardIssuanceTaskTime) {
        // 获取到指定时间之前的所有未发奖成功的task记录
        List<RewardTaskBO> rewardTaskBOList = rewardRepo.findRewardTaskByIsIssuedAndCreateTimeBefore(
                false,
                LocalDateTime.now().minusSeconds(scanAwardIssuanceTaskTime * 2),
                LocalDateTime.now().minusSeconds(scanAwardIssuanceTaskTime)
        );

        // 补偿发奖
        for (RewardTaskBO rewardTaskBO : rewardTaskBOList) {
            sendRewardToMQ(rewardTaskBO);
        }
    }

    /**
     * 插入积分流水
     */
    @Override
    public PointsLogBO insetPointsLog(Long activityId, Long userId, int points, boolean isIssued) {
        return rewardRepo.insertPointsLog(activityId, userId, points, isIssued);
    }

    /**
     * 发送积分奖励消息
     */
    @Override
    public void publishPointsRewardMessage(MQMessage<PointsLogBO> build) {
        rewardRepo.publishPointsRewardMessage(build);
    }

    /**
     * 充值返利账户积分
     */
    @Override
    public void rechargeRewardAccountPoints(Long activityId, Long userId, Long points) {
        rewardRepo.rechargeRewardAccountPoints(activityId, userId, points);
    }

    /**
     * 更新积分流水是否发放
     */
    @Override
    public void updatePointsLogIsIssued(Long pointsLogId, boolean isIssued) {
        rewardRepo.updatePointsLogIsIssued(pointsLogId, isIssued);
    }

    /**
     * 初始化返利账户
     */
    @Override
    public void initRewardAccount(Long userId, long activityId) {
        rewardRepo.initRewardAccount(userId, activityId);
    }

    /**
     * 查询用户积分
     */
    @Override
    public Long findUserRewardAccountPoints(Long userId, Long activityId) {
        RewardAccountBO rewardAccountBO = rewardRepo.findRewardAccountByUserIdAndActivityId(userId, activityId);
        return rewardAccountBO.getPoints();
    }

    /**
     * 查询兑换奖品
     */
    @Override
    public List<ExchangePrizesBO> findExchangePrizes(Long activityId) {
        return rewardRepo.findExchangePrizes(activityId).stream()
                .peek(item -> item.setExchangePrizesIdStr(String.valueOf(item.getExchangePrizesId())))
                .toList();
    }

    /**
     * 兑换奖品
     */
    @Override
    public void exchangePrizes(Long activityId, Long userId, Long exchangePrizesId) {
        // 1. 查询奖品需要多少积分
        rewardRepo.findExchangePrizes(activityId).stream()
                .filter(item -> item.getExchangePrizesId().equals(exchangePrizesId))
                .findFirst()
                .ifPresentOrElse(exchangePrizes -> {
                            // 2. 判断积分是否足够，并消耗积分
                            RewardAccountBO rewardAccountBO = rewardRepo.findRewardAccountByUserIdAndActivityId(userId, activityId);
                            if (exchangePrizes.getPoints() <= rewardAccountBO.getPoints()) {
                                rewardAccountBO.setPoints(rewardAccountBO.getPoints() - exchangePrizes.getPoints());
                                rewardRepo.updateRewardAccount(rewardAccountBO);
                                // 3. 写入流水表
                                rewardRepo.saveExchangePrizesLog(ExchangePrizesLogBO.builder()
                                        .activityId(activityId)
                                        .userId(userId)
                                        .exchangePrizesId(exchangePrizesId)
                                        .build());
                            }
                        },
                        () -> {
                            throw new BigMarketException(BigMarketRespCode.INSUFFICIENT_POINTS);
                        }
                );
    }

    /**
     * 查询兑换奖品历史
     */
    @Override
    public List<ExchangePrizesLogBO> findExchangePrizesLogList(Long activityId, Long userId) {
        return rewardRepo.findExchangePrizesLogList(activityId, userId);
    }

}
