package app.xlog.ggbond.exception;

import app.xlog.ggbond.resp.BigMarketRespCode;
import lombok.*;

/**
 * 本项目异常
 */
@Getter
@Setter
public class BigMarketException extends RuntimeException {
    private final BigMarketRespCode respCode;  // 业务级别的响应码
    private String message;  // 异常信息

    public BigMarketException(BigMarketRespCode respCode) {
        // 如果只传入 respCode，调用 super(respCode.getMessage())
        super(respCode.getMessage());
        this.respCode = respCode;
        this.message = respCode.getMessage();
    }

    public BigMarketException(BigMarketRespCode respCode, String message) {
        // 如果传入了 message，调用 super(message)
        super(message);
        this.respCode = respCode;
        this.message = message;
    }
}