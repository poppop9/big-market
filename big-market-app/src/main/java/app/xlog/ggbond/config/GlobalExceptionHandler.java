package app.xlog.ggbond.config;

import app.xlog.ggbond.BigMarketException;
import app.xlog.ggbond.BigMarketRespCode;
import app.xlog.ggbond.ZakiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BigMarketException.class)
    public ResponseEntity<JsonNode> bigMarketExceptionHandler(BigMarketException e) {
        log.error("业务错误码 :{} --->>> {}",
                e.getRespCode().getCode(),
                e.getMessage() != null ? e.getMessage() : e.getRespCode().getMessage()
        );
        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> {
            System.out.printf("\tat %s.%s(%s:%d) %s%n",
                    stackTraceElement.getClassName(),
                    stackTraceElement.getMethodName(),
                    stackTraceElement.getFileName(),
                    stackTraceElement.getLineNumber(),
                    stackTraceElement.isNativeMethod() ? "~[na:na]" : "~[classes/:na]"
            );
        });

        return ZakiResponse.error(
                e.getRespCode(),
                e.getMessage() != null ? e.getMessage() : e.getRespCode().getMessage()
        );
    }

    /**
     * 处理基本数据类型参数校验异常
     */
    @ExceptionHandler(MethodValidationException.class)
    public ResponseEntity<JsonNode> constrainViolationHandler(MethodValidationException e) {
        String details = e.getAllValidationResults().stream().map(item -> item.getResolvableErrors().stream().findFirst().get().getDefaultMessage()).collect(Collectors.joining(", "));

        System.out.println(e.getClass().getName() + ": " + details + "。" + e.getMessage());
        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> {
            System.out.printf("\tat %s.%s(%s:%d) %s%n",
                    stackTraceElement.getClassName(),
                    stackTraceElement.getMethodName(),
                    stackTraceElement.getFileName(),
                    stackTraceElement.getLineNumber(),
                    stackTraceElement.isNativeMethod() ? "~[na:na]" : "~[classes/:na]"
            );
        });

        return ZakiResponse.error(
                BigMarketRespCode.PARAMETER_VERIFICATION_FAILED,
                details + " --->>> " + e.getMessage()
        );
    }

    /**
     * 处理对象参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> errorMap = fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));

        return ResponseEntity.badRequest().body(errorMap);
    }

}