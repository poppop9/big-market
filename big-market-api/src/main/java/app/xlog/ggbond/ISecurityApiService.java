package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

/**
 * 安全领域 api
 */
public interface ISecurityApiService {

    // 登录
    ResponseEntity<JsonNode> doLogin(Long activityId, Long userId, String password);

    // 查询登录用户的信息
    ResponseEntity<JsonNode> findLoginUserInfo();

    // 通过 token 退出登录
    ResponseEntity<JsonNode> logoutByToken(String token);

}
