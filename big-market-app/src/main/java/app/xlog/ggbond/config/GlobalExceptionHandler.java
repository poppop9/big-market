package app.xlog.ggbond.config;

import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.resp.ZakiResponse;
import cn.dev33.satoken.exception.SaTokenException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
    public ProblemDetail bigMarketExceptionHandler(BigMarketException e) {
        log.error(e.getMessage(), e);
        // todo 待改为自己的业务错误码
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail runtimeExceptionHandler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

}