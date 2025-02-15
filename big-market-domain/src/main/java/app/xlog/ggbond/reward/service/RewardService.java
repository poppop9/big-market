package app.xlog.ggbond.reward.service;

import app.xlog.ggbond.reward.model.RewardTaskBO;
import app.xlog.ggbond.reward.repository.IRewardRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 奖品发放领域 - 奖品发放服务
 */
@Service
public class RewardService implements IRewardService {

    @Resource
    private IRewardRepo rewardRepo;

    /**
     * 奖品发放领域 - 写入task记录
     */
    @Override
    public long insertRewardTask(RewardTaskBO rewardTaskBO) {
        long awardIssuanceId = rewardRepo.insertAwardIssuanceTask(rewardTaskBO);
        return awardIssuanceId;
    }

    /**
     * 奖品发放领域 - 发送奖品发放任务消息
     */
    @Override
    public void sendRewardToMQ(RewardTaskBO rewardTaskBO) {
        rewardRepo.sendAwardIssuanceToMQ(rewardTaskBO);
    }

    /**
     * 奖品发放领域 - 更新奖品发放任务状态
     */
    @Override
    public void updateRewardTaskStatus(Long awardIssuanceId, boolean isIssued) {
        rewardRepo.updateAwardIssuanceTaskStatus(awardIssuanceId, isIssued);
    }

    /**
     * 奖品发放领域 - 扫描task表，补偿未发放奖品的用户
     */
    @Override
    public void scanAndCompensateNotReward(Long scanAwardIssuanceTaskTime) {
        // 获取到指定时间之前的所有未发奖成功的task记录
        List<RewardTaskBO> rewardTaskBOList = rewardRepo.findAwardIssuanceTaskByIsIssuedAndCreateTimeBefore(
                false,
                LocalDateTime.now().minusSeconds(scanAwardIssuanceTaskTime * 2),
                LocalDateTime.now().minusSeconds(scanAwardIssuanceTaskTime)
        );

        // 补偿发奖
        for (RewardTaskBO rewardTaskBO : rewardTaskBOList) {
            sendRewardToMQ(rewardTaskBO);
        }
    }

}
