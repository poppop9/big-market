package app.xlog.ggbond.domain;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.service.filterChain.RaffleFilterChain;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class FilterTest {

    @Resource
    private RaffleFilterChain raffleFilterChain;

/*    @Test
    public void test_FilterChain() throws InterruptedException {
        // 构造过滤器链
        raffleFilterChain.addPreFilter(new BlacklistRaffleFilter())
                .addPreFilter(new RaffleTimesRaffleFilter())
                .addAfterFilter(new InventoryFilter());

        for (int i = 0; i < 10; i++) {
            // 执行过滤器链
            FilterParam filterParam = raffleFilterChain.doFilter(
                    FilterParam.builder()
                            .StrategyId(10001L)
                            .UserId(404L)
                            .build()
            );

            log.atInfo().log("调度了: {}", filterParam.getDispatchParam());
            log.atInfo().log("抽到了: {}", filterParam.getAwardId());
        }

        new CountDownLatch(1).await();
    }*/

    @Test
    void test_2() {
        raffleFilterChain.executeFilterChain(RaffleFilterContext.builder()
                .userId(404L)
                .strategyId(10001L)
                .middleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS)
                .build()
        );
    }
}
