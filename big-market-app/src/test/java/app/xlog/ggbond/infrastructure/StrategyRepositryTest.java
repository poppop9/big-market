package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.repository.RaffleRepository;
import app.xlog.ggbond.raffle.repository.IAwardInventoryRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StrategyRepositryTest {

    @Resource
    private IAwardInventoryRepository awardInventoryRepository;

    @Test
    public void test_removeAwardFromPools() {
        // 去掉随机积分奖品
        awardInventoryRepository.removeAwardFromPools(10001, 101);
    }
}
