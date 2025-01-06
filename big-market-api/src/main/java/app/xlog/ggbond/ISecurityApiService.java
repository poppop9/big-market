package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

/**
 * 安全领域 - 用户权限api
 */
public interface ISecurityApiService {

    ResponseEntity<JsonNode> doLogin(Long activityId, Long userId, String password) throws Exception;

}
