package app.xlog.ggbond.domain;

import app.xlog.ggbond.strategy.model.vo.FilterParam;
import app.xlog.ggbond.strategy.service.filter.BlacklistRaffleFilter;
import app.xlog.ggbond.strategy.service.filter.InventoryFilter;
import app.xlog.ggbond.strategy.service.filter.RaffleFilterChain;
import app.xlog.ggbond.strategy.service.filter.RaffleTimesRaffleFilter;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;


@SpringBootTest
public class FilterTest {
    private static final Logger log = LoggerFactory.getLogger(FilterTest.class);

    @Resource
    private RaffleFilterChain raffleFilterChain;

    @Test
    public void test_FilterChain() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            // 构造过滤器链
            raffleFilterChain.addPreFilter(new BlacklistRaffleFilter())
                    .addPreFilter(new RaffleTimesRaffleFilter())
                    .addAfterFilter(new InventoryFilter());

            // todo 后置过滤器 - 库存过滤器，添加到队列中的数据过多了，有问题
            // 执行过滤器链
            FilterParam filterParam = raffleFilterChain.doFilter(
                    FilterParam.builder()
                            .StrategyId(10001)
                            .UserId(404)
                            .build()
            );

            log.atInfo().log("调度了: {}", filterParam.getDispatchParam());
            log.atInfo().log("抽到了: {}", filterParam.getAwardId());
        }

        new CountDownLatch(1).await();
    }
}
