package app.xlog.ggbond.raffle.service.filterChain.filters;

/**
 * 抽奖重新调度异常
 */
public class RetryRouterException extends RuntimeException {
    public RetryRouterException(String message) {
        super(message);
    }
}
