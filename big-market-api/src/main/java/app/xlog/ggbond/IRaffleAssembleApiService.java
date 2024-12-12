package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 抽奖领域 - 装配api
 */
public interface IRaffleAssembleApiService {

    public Response<JsonNode> assembleRaffleAll(Long strategyId);

}
