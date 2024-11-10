package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

public interface IRaffleApiService {

    public Response<JsonNode> queryAwardList(Long strategyId);

    public Response<JsonNode> getAward(Long userId, Long strategyId);

}
