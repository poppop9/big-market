package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleDispatchApiService;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.resp.ZakiResponse;
import app.xlog.ggbond.integrationService.TriggerService;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 抽奖领域 - 调度
 */
@Slf4j
@RestController
@RequestMapping("/api/raffle/dispatch")
public class RaffleDispatchController implements IRaffleDispatchApiService {

    @Resource
    private TriggerService triggerService;
    @Resource
    private transient ISecurityService securityService;

    /**
     * 抽奖
     */
    @Override
    @SneakyThrows
    @GetMapping("/v2/raffle")
    public ResponseEntity<JsonNode> raffle(@RequestParam Long activityId) {
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        AwardBO awardBO = triggerService.raffle(StpUtil.getSession(),user, activityId);
        return ZakiResponse.ok("awardBO", awardBO);
    }

}