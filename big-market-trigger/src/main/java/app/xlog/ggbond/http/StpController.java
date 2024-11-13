package app.xlog.ggbond.http;

import app.xlog.ggbond.IStpApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.user.service.IUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户权限
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class StpController implements IStpApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IUserService userService;

    /**
     * 登录
     */
    @Override
    @GetMapping("/v1/doLogin")
    public Response<JsonNode> doLogin(Long userId, String password) {
        Boolean isSuccess = userService.doLogin(userId, password);
        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(isSuccess ? objectMapper.valueToTree("登录成功") : objectMapper.valueToTree("登录失败"))
                .build();
    }

}
