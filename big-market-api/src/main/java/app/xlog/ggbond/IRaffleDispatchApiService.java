package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 抽奖领域 - 调度api
 */
public interface IRaffleDispatchApiService {

    // 根据策略id，抽取奖品
    ResponseEntity<JsonNode> raffle(Long strategyId);

    // 获取中奖列表
    ResponseEntity<JsonNode> getWinningAwardsInfo(Long activityId);

}
