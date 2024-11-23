package app.xlog.ggbond.raffle.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public interface IRaffleService {

    // 根据策略id，查询对应的所有奖品
    List<ObjectNode> findAwardsByStrategyId(Long strategyId);

    Long getAward(Long userId, Long strategyId);

}
