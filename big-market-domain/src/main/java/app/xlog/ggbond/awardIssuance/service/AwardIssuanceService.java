package app.xlog.ggbond.awardIssuance.service;

import app.xlog.ggbond.awardIssuance.model.AwardIssuanceTaskBO;
import app.xlog.ggbond.awardIssuance.repository.IAwardIssuanceRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 奖品发放领域 - 奖品发放服务
 */
@Service
public class AwardIssuanceService implements IAwardIssuanceService{

    @Resource
    private IAwardIssuanceRepo awardIssuanceRepo;

    /**
     * 奖品发放领域 - 写入task记录
     */
    @Override
    public long insertAwardIssuanceTask(AwardIssuanceTaskBO awardIssuanceTaskBO) {
        long awardIssuanceId = awardIssuanceRepo.insertAwardIssuanceTask(awardIssuanceTaskBO);
        return awardIssuanceId;
    }

    /**
     * 奖品发放领域 - 发送奖品发放任务消息
     */
    @Override
    public void sendAwardIssuanceToMQ(AwardIssuanceTaskBO awardIssuanceTaskBO) {
        awardIssuanceRepo.sendAwardIssuanceToMQ(awardIssuanceTaskBO);
    }

    /**
     * 奖品发放领域 - 更新奖品发放任务状态
     */
    @Override
    public void updateAwardIssuanceTaskStatus(Long awardIssuanceId, boolean isIssued) {
        awardIssuanceRepo.updateAwardIssuanceTaskStatus(awardIssuanceId, isIssued);
    }

}
