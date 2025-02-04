package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

/**
 * 抽奖领域 - 调度api
 */
public interface IRaffleDispatchApiService {

    // 根据策略id，抽取奖品
    ResponseEntity<JsonNode> raffle(Long strategyId);

}
