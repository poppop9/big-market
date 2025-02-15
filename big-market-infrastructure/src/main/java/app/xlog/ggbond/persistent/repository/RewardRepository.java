package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.reward.model.RewardTaskBO;
import app.xlog.ggbond.reward.repository.IRewardRepo;
import app.xlog.ggbond.mq.MQEventCenter;
import app.xlog.ggbond.persistent.po.reward.RewardTask;
import app.xlog.ggbond.persistent.repository.jpa.RewardTaskJpa;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 奖品发放领域 - 奖品发放仓储实现类
 */
@Repository
public class RewardRepository implements IRewardRepo {

    @Resource
    private MQEventCenter mqEventCenter;
    @Resource
    private RewardTaskJpa rewardTaskJpa;

    /**
     * 奖品发放领域 - 写入task记录
     */
    @Override
    public long insertAwardIssuanceTask(RewardTaskBO rewardTaskBO) {
        RewardTask rewardTask = rewardTaskJpa.save(RewardTask.builder()
                .userId(rewardTaskBO.getUserId())
                .userRaffleHistoryId(rewardTaskBO.getUserRaffleHistoryId())
                .isIssued(false)
                .build()
        );
        return rewardTask.getRewardId();
    }

    /**
     * 奖品发放领域 - 发送奖品发放任务
     */
    @Override
    public void sendAwardIssuanceToMQ(RewardTaskBO rewardTaskBO) {
        mqEventCenter.sendMessage(GlobalConstant.KafkaConstant.REWARD_TASK,
                MQMessage.<RewardTaskBO>builder()
                        .data(rewardTaskBO)
                        .build()
        );
    }

    /**
     * 奖品发放领域 - 更新奖品发放任务状态
     */
    @Override
    public void updateAwardIssuanceTaskStatus(Long awardIssuanceId, boolean isIssued) {
        rewardTaskJpa.updateIsIssuedByAwardIssuanceId(isIssued, awardIssuanceId);
    }

    /**
     * 奖品发放领域 - 查询所有指定时间内，未发放奖品的记录
     */
    @Override
    public List<RewardTaskBO> findAwardIssuanceTaskByIsIssuedAndCreateTimeBefore(boolean isIssued, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<RewardTask> rewardTaskList = rewardTaskJpa.findByIsIssuedAndCreateTimeBetween(
                isIssued,
                startDateTime,
                endDateTime
        );
        return BeanUtil.copyToList(rewardTaskList, RewardTaskBO.class);
    }

}
