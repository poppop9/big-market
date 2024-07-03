package app.xlog.ggbond.domain;

import app.xlog.ggbond.strategy.service.armory.IStrategyArmory;
import app.xlog.ggbond.strategy.service.armory.IStrategyDispatch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StrategyArmoryDispatchTest {
    @Autowired
    private IStrategyArmory strategyArmory;
    @Autowired
    private IStrategyDispatch strategyDispatch;

    Logger logger = LoggerFactory.getLogger(StrategyArmoryDispatchTest.class);

    // 测试装配对应策略的奖品，和装配奖品的权重对象
    @Test
    public void testAssembleLotteryStrategy() {
        strategyArmory.assembleLotteryStrategy(10001);
    }

    // 测试获取随机的奖品ID
    @Test
    public void testGetRandomAwardId() {
        Integer randomAwardId;

        for (int i = 0; i < 50; i++) {
            randomAwardId = strategyDispatch.getRandomAwardId(10001);
            logger.atInfo().log("中奖的奖品id : {}", randomAwardId);
        }
    }
}
