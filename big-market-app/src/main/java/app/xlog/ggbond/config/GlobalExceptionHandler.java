package app.xlog.ggbond.config;

import app.xlog.ggbond.model.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<JsonNode> constrainViolationHandler(ConstraintViolationException e) {
        log.error("参数校验异常 : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(objectMapper.createObjectNode()
                        .put("message", e.getMessage())
                );
    }

    /**
     * 处理基本数据类型参数校验异常
     */
    @ExceptionHandler(MethodValidationException.class)
    public ResponseEntity<JsonNode> constrainViolationHandler(MethodValidationException e) {
        log.error("参数校验异常 : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(objectMapper.createObjectNode()
                        .put("message",
                                e.getMessage() + " : " + e.getAllValidationResults().stream()
                                        .map(item -> item.getResolvableErrors().stream().findFirst().get().getDefaultMessage())
                                        .collect(Collectors.joining(", "))
                        )
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