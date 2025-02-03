package app.xlog.ggbond.job;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.awardIssuance.service.IAwardIssuanceService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 奖品发放领域 - 任务
 */
@Slf4j
@Component
public class AwardIssuanceJob {

    @Resource
    private IAwardIssuanceService awardIssuanceService;

    /**
     * 扫描task表，补偿未发放奖品的用户
     * todo 未测试
     */
    @XxlJob("scan_and_compensate_not_issuance_award")
    public void scanAndCompensateNotIssuanceAward() {
        awardIssuanceService.scanAndCompensateNotIssuanceAward(
                GlobalConstant.SCAN_AWARD_ISSUANCE_TASK_TIME
        );
    }

}
