package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleDispatchApiService;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.resp.ZakiResponse;
import app.xlog.ggbond.integrationService.TriggerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抽奖领域 - 调度
 */
@Slf4j
@RestController
@RequestMapping("/api/raffle/dispatch")
public class RaffleDispatchController implements IRaffleDispatchApiService {

    @Resource
    private TriggerService triggerService;

    /**
     * 抽奖
     */
    @Override
    @GetMapping("/v2/raffle")
    public ResponseEntity<JsonNode> raffle(@RequestParam Long activityId) {
        AwardBO awardBO = triggerService.raffle(activityId);
        return ZakiResponse.ok("awardBO", awardBO);
    }

}
