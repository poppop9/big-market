package app.xlog.ggbond.config;

import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.resp.ZakiResponse;
import cn.dev33.satoken.exception.SaTokenException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
@RestController
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BigMarketException.class)
    public ResponseEntity<JsonNode> bigMarketExceptionHandler(BigMarketException e) {
        return ZakiResponse.error(
                e.getRespCode(),
                e.getMessage() != null ? e.getMessage() : e.getRespCode().getMessage()
        );
    }

    /**
     * 处理登录相关异常
     */
    @ExceptionHandler(SaTokenException.class)
    public ResponseEntity<JsonNode> saTokenExceptionHandler(SaTokenException e) {
        return ZakiResponse.error(e.getMessage());
    }

}