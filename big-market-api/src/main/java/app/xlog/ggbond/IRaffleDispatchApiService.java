package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 抽奖领域 - 调度api
 */
public interface IRaffleDispatchApiService {

    // 根据策略id，查询对应的奖品列表
    @Deprecated
    Response<JsonNode> queryAwardList(Long strategyId);

    // 查询对应的奖品列表
    Response<JsonNode> queryAwardList();

    // 根据策略id，抽取奖品
    @Deprecated
    Response<JsonNode> getAward(Long strategyId);

    // 抽取奖品
    Response<JsonNode> getAward();

}
