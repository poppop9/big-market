package app.xlog.ggbond.raffle.model.vo;

import app.xlog.ggbond.BigMarketRespCode;
import lombok.Getter;

/**
 * 抽奖重试调度异常
 */
@Getter
public class RetryRouterException extends RuntimeException {

    private BigMarketRespCode respCode;

    public RetryRouterException(BigMarketRespCode respCode, String message) {
        super(message);
        this.respCode = respCode;
    }

}
