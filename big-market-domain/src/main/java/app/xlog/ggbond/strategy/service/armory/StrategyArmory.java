package app.xlog.ggbond.strategy.service.armory;

import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StrategyArmory implements IStrategyArmory {
    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public void assembleLotteryStrategy(int strategyId) {
        // 查询对应策略的所有奖品
        List<AwardBO> awards = strategyRepository.queryAwards(strategyId);


    }
}
