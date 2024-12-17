package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 抽奖领域 - 调度api
 */
public interface IRaffleDispatchApiService {

    // 根据策略id，查询对应的奖品列表
    Response<JsonNode> queryAwardList(Long strategyId);

    // 根据策略id，抽取奖品
    Response<JsonNode> getAward(Long strategyId);

}
