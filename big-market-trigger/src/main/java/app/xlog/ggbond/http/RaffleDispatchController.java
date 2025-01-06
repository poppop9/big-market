package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleDispatchApiService;
import app.xlog.ggbond.model.JsonResult;
import app.xlog.ggbond.raffleAndSecurity.RaffleSecurityAppService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抽奖领域 - 调度接口
 */
@Slf4j
@RestController
@RequestMapping("/api/raffle/dispatch")
public class RaffleDispatchController implements IRaffleDispatchApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RaffleSecurityAppService raffleSecurityAppService;

    /**
     * 抽取奖品
     **/
    @Override
    @GetMapping("/v2/getAward")
    public ResponseEntity<JsonNode> getAward(@RequestParam Long activityId) {
        Long awardId = raffleSecurityAppService.dispatchAwardIdByActivityIdAndCurrentUser(activityId);
        return JsonResult.ok("awardId", awardId);
    }

}
