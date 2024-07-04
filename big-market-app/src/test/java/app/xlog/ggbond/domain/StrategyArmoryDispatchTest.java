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
        strategyArmory.assembleLotteryStrategyRuleCommon(10001);
        System.out.println("装配策略10001的全奖品完成");

        strategyArmory.assembleLotteryStrategyRuleLock(10001);
        System.out.println("装配策略10001的除锁定奖品完成");

        strategyArmory.assembleLotteryStrategyRuleLockLong(10001);
        System.out.println("装配策略10001的除最后一个奖品完成");
    }

    // 测试获取所有奖品中的随机奖品ID
    @Test
    public void testGetRandomRuleCommonAwardId() {
        Integer randomAwardId;

        for (int i = 0; i < 50; i++) {
            randomAwardId = strategyDispatch.getRuleCommonAwardIdByRandom(10001);
            logger.atInfo().log("中奖的奖品id : {}", randomAwardId);
        }
    }

    // 测试获取除锁定奖品中的随机奖品ID
    @Test
    public void testGetRandomRuleLockAwardId() {
        Integer randomAwardId;

        for (int i = 0; i < 50; i++) {
            randomAwardId = strategyDispatch.getRuleLockAwardIdByRandom(10001);
            logger.atInfo().log("中奖的奖品id : {}", randomAwardId);
        }
    }

    // 测试获取除最后一个奖品中的随机奖品ID
    @Test
    public void testGetRandomRuleLockLongAwardId() {
        Integer randomAwardId;

        for (int i = 0; i < 50; i++) {
            randomAwardId = strategyDispatch.getRuleLockLongAwardIdByRandom(10001);
            logger.atInfo().log("中奖的奖品id : {}", randomAwardId);
        }
    }
}
