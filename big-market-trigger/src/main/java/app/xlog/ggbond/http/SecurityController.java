package app.xlog.ggbond.http;

import app.xlog.ggbond.ISecurityApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.security.service.ISecurityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户权限
 */
@Slf4j
@RestController
@RequestMapping("/api/security/user")
public class SecurityController implements ISecurityApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ISecurityService securityService;

    /**
     * 登录
     */
    @Override
    @GetMapping("/v1/doLogin")
    public Response<JsonNode> doLogin(@RequestParam Long userId, @RequestParam String password) {
        Boolean isSuccess = securityService.doLogin(userId, password);
        if (!isSuccess) {
            throw new RuntimeException("用户名或密码错误");
        }

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree("登录成功"))
                .build();
    }

}
