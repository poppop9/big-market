package app.xlog.ggbond.http;

import app.xlog.ggbond.IRewardApiService;
import app.xlog.ggbond.resp.ZakiResponse;
import app.xlog.ggbond.reward.model.ExchangePrizesLogBO;
import app.xlog.ggbond.reward.service.IRewardService;
import app.xlog.ggbond.security.service.ISecurityService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 返利领域
 */
@Slf4j
@RestController
@RequestMapping("/api/reward")
public class RewardController implements IRewardApiService {

    @Resource
    private IRewardService rewardService;
    @Resource
    private ISecurityService securityService;

    /**
     * 查询用户积分
     */
    @Override
    @GetMapping("/v1/findUserRewardAccountPoints")
    public ResponseEntity<JsonNode> findUserRewardAccountPoints(Long activityId) {
        Long userId = securityService.getLoginIdDefaultNull();
        return ZakiResponse.ok(
                "userRewardAccountPoints",
                rewardService.findUserRewardAccountPoints(userId, activityId)
        );
    }

    /**
     * 查询兑换奖品
     */
    @Override
    @GetMapping("/v1/findExchangePrizes")
    public ResponseEntity<JsonNode> findExchangePrizes(Long activityId) {
        return ZakiResponse.ok("exchangePrizes", rewardService.findExchangePrizes(activityId));
    }

    /**
     * 兑换奖品
     */
    @Override
    @GetMapping("/v1/exchangePrizes")
    public ResponseEntity<JsonNode> exchangePrizes(Long activityId, Long exchangePrizesId) {
        rewardService.exchangePrizes(
                activityId,
                securityService.getLoginIdDefaultNull(),
                exchangePrizesId
        );
        return ZakiResponse.ok("兑换奖品成功");
    }

    /**
     * 查询兑换奖品历史
     */
    @Override
    @GetMapping("/v1/findExchangePrizesLogList")
    public ResponseEntity<JsonNode> findExchangePrizesLogList(Long activityId) {
        List<ExchangePrizesLogBO> exchangePrizesLogBOList = rewardService.findExchangePrizesLogList(
                activityId, securityService.getLoginIdDefaultNull()
        );
        return ZakiResponse.ok("exchangePrizesLogBOList", exchangePrizesLogBOList);
    }

}