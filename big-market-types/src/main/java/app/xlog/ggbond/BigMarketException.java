package app.xlog.ggbond;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 本项目异常
 */
@Data
@AllArgsConstructor
public class BigMarketException extends RuntimeException {
    private BigMarketRespCode respCode;  // 业务级别的响应码
    private String message;  // 异常信息
}