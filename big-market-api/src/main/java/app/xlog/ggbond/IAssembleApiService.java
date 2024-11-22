package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

public interface IAssembleApiService {

    public Response<JsonNode> assembleRaffleAll(Long strategyId);

}
