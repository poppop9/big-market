package app.xlog.ggbond.job;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.reward.service.IRewardService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 返利领域 - 任务
 */
@Slf4j
@Component
public class RewardJob {

    @Resource
    private IRewardService rewardService;

    /**
     * 扫描task表，补偿未发放奖品的用户
     */
    @XxlJob("scan_and_compensate_not_reward")
    public void scanAndCompensateNotReward() {
        rewardService.scanAndCompensateNotReward(
                GlobalConstant.SCAN_REWARD_TASK_TIME
        );
    }

}
