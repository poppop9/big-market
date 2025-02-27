package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

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

    // 读取excel，写入用户购买历史
    ResponseEntity<JsonNode> writePurchaseHistoryFromExcel(MultipartFile file);

}
