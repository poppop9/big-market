package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.awardIssuance.model.AwardIssuanceTaskBO;
import app.xlog.ggbond.awardIssuance.repository.IAwardIssuanceRepo;
import app.xlog.ggbond.mq.MQEventCenter;
import app.xlog.ggbond.persistent.po.awardIssuance.AwardIssuanceTask;
import app.xlog.ggbond.persistent.repository.jpa.AwardIssuanceTaskJpa;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * 奖品发放领域 - 奖品发放仓储实现类
 */
@Repository
public class AwardIssuanceRepository implements IAwardIssuanceRepo {

    @Resource
    private MQEventCenter mqEventCenter;
    @Resource
    private AwardIssuanceTaskJpa awardIssuanceTaskJpa;

    /**
     * 奖品发放领域 - 写入task记录
     */
    @Override
    public void insertAwardIssuanceTask(AwardIssuanceTaskBO awardIssuanceTaskBO) {
        awardIssuanceTaskJpa.save(AwardIssuanceTask.builder()
                        .userId(awardIssuanceTaskBO.getUserId())
                        .userRaffleHistoryId(awardIssuanceTaskBO.getUserRaffleHistoryId())
                        .isIssued(false)
                        .build()
        );
    }

    /**
     * 奖品发放领域 - 发送奖品发放任务
     */
    @Override
    public void sendAwardIssuanceToMQ(AwardIssuanceTaskBO awardIssuanceTaskBO) {
        mqEventCenter.sendMessage(
                GlobalConstant.KafkaConstant.AWARD_ISSUANCE_TASK,
                MQMessage.<AwardIssuanceTaskBO>builder()
                        .data(awardIssuanceTaskBO)
                        .build()
        );
    }

    /**
     * 奖品发放领域 - 更新奖品发放任务状态
     */
    @Override
    public void updateAwardIssuanceTaskStatus(Long awardIssuanceId, boolean isIssued) {
        awardIssuanceTaskJpa.updateIsIssuedByAwardIssuanceId(isIssued, awardIssuanceId);
    }

}
