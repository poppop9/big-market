package app.xlog.ggbond.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(objectMapper.createObjectNode()
                        .put("message", details + "。" + e.getMessage())
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