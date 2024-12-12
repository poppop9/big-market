package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 抽奖领域 - 调度api
 */
public interface IRaffleDispatchApiService {

    public Response<JsonNode> queryAwardList(Long strategyId);

    public Response<JsonNode> getAward(Long strategyId);

}
