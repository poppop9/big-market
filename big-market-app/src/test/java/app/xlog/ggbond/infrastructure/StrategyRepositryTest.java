package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.repository.StrategyRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StrategyRepositryTest {

    @Resource
    private StrategyRepository strategyRepository;

    @Test
    public void test_removeAwardFromPools() {
        // 去掉随机积分奖品
        strategyRepository.removeAwardFromPools(10001, 101);
    }
}
