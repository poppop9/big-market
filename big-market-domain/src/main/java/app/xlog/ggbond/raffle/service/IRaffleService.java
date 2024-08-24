package app.xlog.ggbond.raffle.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public interface IRaffleService {
    List<ObjectNode> queryAwardList(Integer strategyId);

    Integer getAward(Integer userId, Integer strategyId);
}
