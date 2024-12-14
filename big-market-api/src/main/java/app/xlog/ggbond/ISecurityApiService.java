package app.xlog.ggbond;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 安全领域 - 用户权限api
 */
public interface ISecurityApiService {

    public Response<JsonNode> doLogin(Long userId, String password) throws Exception;

}
