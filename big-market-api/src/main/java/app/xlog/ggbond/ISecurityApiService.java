package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 用户权限
 */
public interface ISecurityApiService {

    public Response<JsonNode> doLogin(Long userId, String password);

}
