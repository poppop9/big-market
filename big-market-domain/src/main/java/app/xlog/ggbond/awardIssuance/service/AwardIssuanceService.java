package app.xlog.ggbond.awardIssuance.service;

import app.xlog.ggbond.awardIssuance.model.AwardIssuanceTaskBO;
import app.xlog.ggbond.awardIssuance.repository.IAwardIssuanceRepo;
import jakarta.annotation.Resource;

/**
 * 奖品发放领域 - 奖品发放服务
 */
public class AwardIssuanceService implements IAwardIssuanceService{

    @Resource
    private IAwardIssuanceRepo awardIssuanceRepo;

    /**
     * 奖品发放领域 - 写入task记录
     * todo 未测试
     */
    @Override
    public void insertAwardIssuanceTask(AwardIssuanceTaskBO awardIssuanceTaskBO) {
        awardIssuanceRepo.insertAwardIssuanceTask(awardIssuanceTaskBO);
    }

    /**
     * 奖品发放领域 - 发送奖品发放任务消息
     * todo 未测试
     */
    @Override
    public void sendAwardIssuanceToMQ(AwardIssuanceTaskBO awardIssuanceTaskBO) {
        awardIssuanceRepo.sendAwardIssuanceToMQ(awardIssuanceTaskBO);
    }

    /**
     * 奖品发放领域 - 更新奖品发放任务状态
     * todo 未测试
     */
    @Override
    public void updateAwardIssuanceTaskStatus(Long awardIssuanceId, boolean isIssued) {
        awardIssuanceRepo.updateAwardIssuanceTaskStatus(awardIssuanceId, isIssued);
    }

}
