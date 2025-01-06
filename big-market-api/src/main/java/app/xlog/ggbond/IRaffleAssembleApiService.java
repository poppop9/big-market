package app.xlog.ggbond;

import app.xlog.ggbond.model.JsonResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

/**
 * 抽奖领域 - 装配api
 */
public interface IRaffleAssembleApiService {

    // 根据策略id，查询对应的奖品列表
    ResponseEntity<JsonNode> queryAwardList(Long strategyId);

    // 实时获取中奖奖品信息
    Flux<ResponseEntity<JsonNode>> getWinningAwardsInfo(@RequestParam Long activityId);

}
