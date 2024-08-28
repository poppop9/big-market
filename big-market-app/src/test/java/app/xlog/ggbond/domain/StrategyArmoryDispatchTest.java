package app.xlog.ggbond.domain;

import app.xlog.ggbond.raffle.service.armory.IRaffleArmory;
import app.xlog.ggbond.raffle.service.armory.IRaffleDispatch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StrategyArmoryDispatchTest {
    @Autowired
    private IRaffleArmory strategyArmory;
    @Autowired
    private IRaffleDispatch strategyDispatch;

    Logger logger = LoggerFactory.getLogger(StrategyArmoryDispatchTest.class);

    // 装配对应策略的奖品，装配奖品的权重对象，装配所有对应策略的奖品库存
    // todo 为什么没有装配，会把抽奖池里的奖品移除
    @Test
    public void testAssembleLotteryStrategy() {
        strategyArmory.assembleLotteryStrategyRuleCommon(10001);
        System.out.println("装配策略10001的全奖品完成");

        strategyArmory.assembleLotteryStrategyRuleLock(10001);
        System.out.println("装配策略10001的除锁定奖品完成");

        strategyArmory.assembleLotteryStrategyRuleLockLong(10001);
        System.out.println("装配策略10001的除最后一个奖品完成");

        strategyArmory.assembleLotteryStrategyRuleGrand(10001);
        System.out.println("装配策略10001的大奖池完成");

        strategyArmory.assembleLotteryStrategyAwardCount(10001);
        System.out.println("装配策略10001的奖品库存完成");
    }

    // 测试获取所有奖品中的随机奖品ID
    @Test
    public void testGetRandomRuleCommonAwardId() {
        Integer randomAwardId;

        randomAwardId = strategyDispatch.getRuleCommonAwardIdByRandom(10001);
        logger.atInfo().log("中奖的奖品id : {}", randomAwardId);
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

    // 测试获取针对于黑名单用户的，最差的奖品
    @Test
    public void testGetWorstAwardId() {
        Integer worstAwardId = strategyDispatch.getWorstAwardId(10001);
        logger.atInfo().log("黑名单用户的奖品id : {}", worstAwardId);
    }

    @Test
    public void testGetGrandAwardIdByRandom() {
        for (int i = 0; i < 100; i++) {
            Integer grandAwardId = strategyDispatch.getRuleGrandAwardIdByRandom(10001);
            logger.atInfo().log("大奖池中的奖品id : {}", grandAwardId);
        }
    }
}
