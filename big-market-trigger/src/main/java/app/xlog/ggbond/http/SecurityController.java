package app.xlog.ggbond.http;

import app.xlog.ggbond.ISecurityApiService;
import app.xlog.ggbond.resp.ZakiResponse;
import app.xlog.ggbond.integrationService.TriggerService;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.service.ISecurityService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 安全领域
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/security/user")
public class SecurityController implements ISecurityApiService {

    @Resource
    private TriggerService triggerService;
    @Resource
    private ISecurityService securityService;

    /**
     * 登录
     *
     * @param activityId 这个参数用来鉴定是哪个活动的登录
     * @param userId     用户id
     * @param password   密码
     */
    @Override
    @GetMapping("/v1/doLogin")
    public ResponseEntity<JsonNode> doLogin(@RequestParam Long activityId, @RequestParam Long userId, @RequestParam String password) {
        UserBO userBO = triggerService.doLogin(userId, password);
        return ZakiResponse.ok("userInfo", userBO);
    }

    /**
     * 通过 token 退出登录
     */
    @Override
    @DeleteMapping("/v1/logoutByToken")
    public ResponseEntity<JsonNode> logoutByToken(@NotBlank(message = "token 不能是无效文本") String token) {
        securityService.logoutByToken(token);
        return ZakiResponse.ok("");
    }

}
