package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

public interface IRaffleApiService {

    public Response<JsonNode> queryAwardList(Integer strategyId);

    public Response<JsonNode> getAward(Integer userId, Integer strategyId);

}
