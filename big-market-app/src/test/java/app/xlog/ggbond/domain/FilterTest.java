package app.xlog.ggbond.domain;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.service.filter.BlacklistRaffleFilter;
import app.xlog.ggbond.raffle.service.filter.InventoryFilter;
import app.xlog.ggbond.raffle.service.filter.RaffleFilterChain;
import app.xlog.ggbond.raffle.service.filter.RaffleTimesRaffleFilter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootTest
public class FilterTest {

    @Resource
    private RaffleFilterChain raffleFilterChain;

    @Test
    public void test_FilterChain() throws InterruptedException {
        // 构造过滤器链
        raffleFilterChain.addPreFilter(new BlacklistRaffleFilter())
                .addPreFilter(new RaffleTimesRaffleFilter())
                .addAfterFilter(new InventoryFilter());

        for (int i = 0; i < 10; i++) {
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
