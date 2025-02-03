package app.xlog.ggbond.awardIssuance.repository;

import app.xlog.ggbond.awardIssuance.model.AwardIssuanceTaskBO;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleHistoryBO;
import cn.hutool.core.lang.WeightRandom;

import java.util.List;
import java.util.Map;

/**
 * 奖品发放领域 - 奖品发放仓储
 */
public interface IAwardIssuanceRepo {

    // 新增 - 写入task记录
    void insertAwardIssuanceTask(AwardIssuanceTaskBO awardIssuanceTaskBO);

    // 新增 - 发送奖品发放任务
    void sendAwardIssuanceToMQ(AwardIssuanceTaskBO awardIssuanceTaskBO);

    // 更新 - 更新奖品发放任务状态
    void updateAwardIssuanceTaskStatus(Long awardIssuanceId, boolean isIssued);

}
