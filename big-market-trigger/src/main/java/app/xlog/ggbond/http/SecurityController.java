package app.xlog.ggbond.http;

import app.xlog.ggbond.ISecurityApiService;
import app.xlog.ggbond.ZakiResponse;
import app.xlog.ggbond.integrationService.RaffleSecurityAppService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 安全领域 - 用户权限
 */
@Slf4j
@RestController
@RequestMapping("/api/security/user")
public class SecurityController implements ISecurityApiService {

    @Resource
    private RaffleSecurityAppService raffleSecurityAppService;

    /**
     * 登录 todo 已完成
     * @param activityId 这个参数用来鉴定是哪个活动的登录
     * @param userId     用户id
     * @param password   密码
     */
    @Override
    @GetMapping("/v1/doLogin")
    public ResponseEntity<JsonNode> doLogin(@RequestParam Long activityId, @RequestParam Long userId, @RequestParam String password) {
        raffleSecurityAppService.doLogin(userId, password);
        return ZakiResponse.ok("用户 " + userId + " 登录成功");
    }

}
