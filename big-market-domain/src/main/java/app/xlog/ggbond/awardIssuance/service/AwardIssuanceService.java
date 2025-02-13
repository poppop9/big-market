package app.xlog.ggbond.awardIssuance.service;

import app.xlog.ggbond.awardIssuance.model.AwardIssuanceTaskBO;
import app.xlog.ggbond.awardIssuance.repository.IAwardIssuanceRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 奖品发放领域 - 奖品发放服务
 */
@Service
public class AwardIssuanceService implements IAwardIssuanceService {

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

    /**
     * 奖品发放领域 - 扫描task表，补偿未发放奖品的用户
     */
    @Override
    public void scanAndCompensateNotIssuanceAward(Long scanAwardIssuanceTaskTime) {
        // 获取到指定时间之前的所有未发奖成功的task记录
        List<AwardIssuanceTaskBO> awardIssuanceTaskBOList = awardIssuanceRepo.findAwardIssuanceTaskByIsIssuedAndCreateTimeBefore(
                false,
                LocalDateTime.now().minusSeconds(scanAwardIssuanceTaskTime * 2),
                LocalDateTime.now().minusSeconds(scanAwardIssuanceTaskTime)
        );

        // 补偿发奖
        for (AwardIssuanceTaskBO awardIssuanceTaskBO : awardIssuanceTaskBOList) {
            sendAwardIssuanceToMQ(awardIssuanceTaskBO);
        }
    }

}
