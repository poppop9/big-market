package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

/**
 * 抽奖领域 - 调度api
 */
public interface IRaffleDispatchApiService {

    // 根据策略id，查询对应的奖品列表
    Response<JsonNode> queryAwardList(Long strategyId);

    // 根据策略id，抽取奖品
    Response<JsonNode> getAward(Long strategyId);

    // 实时获取中奖奖品信息
    Flux<Response<JsonNode>> getWinningAwardsInfo(@RequestParam Long activityId);

}
