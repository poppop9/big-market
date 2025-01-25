package app.xlog.ggbond.exception;

import app.xlog.ggbond.resp.BigMarketRespCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽奖重试调度异常
 */
@Getter
public class RetryRouterException extends BigMarketException {

    private final BigMarketRespCode respCode;
    private String message;

    public RetryRouterException(BigMarketRespCode respCode) {
        super(respCode);
        this.respCode = respCode;
    }

    public RetryRouterException(BigMarketRespCode respCode, String message) {
        super(respCode, message);
        this.respCode = respCode;
        this.message = message;
    }

}
