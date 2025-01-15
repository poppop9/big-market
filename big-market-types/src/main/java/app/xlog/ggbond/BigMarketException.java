package app.xlog.ggbond;

import lombok.*;

/**
 * 本项目异常
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class BigMarketException extends RuntimeException {
    private final BigMarketRespCode respCode;  // 业务级别的响应码
    private String message;  // 异常信息
}