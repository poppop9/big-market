package app.xlog.ggbond.raffle.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public interface IRaffleService {

    List<ObjectNode> queryAwardList(Long strategyId);

    Long getAward(Long userId, Long strategyId);

}
