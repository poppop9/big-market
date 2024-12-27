package app.xlog.ggbond.raffle.model.vo;

/**
 * 抽奖重试调度异常
 */
public class RetryRouterException extends RuntimeException {
    public RetryRouterException(String message) {
        super(message);
    }
}
