package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import app.xlog.ggbond.raffle.service.filter.RaffleFilterChain;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class RaffleService implements IRaffleService {

    private static final Logger log = LoggerFactory.getLogger(RaffleService.class);
    @Resource
    private IRaffleRepository raffleRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RaffleFilterChain raffleFilterChain;

    @Override
    public List<ObjectNode> queryAwardList(Integer strategyId) {
        return raffleRepository.queryCommonAwards(strategyId).stream()
                .map(awardBO -> {
                    try {
                        ObjectNode objectNode = objectMapper.valueToTree(awardBO);
                        ObjectNode rulesNode = awardBO.stringToObjectNode(objectNode.get("rules").asText());
                        objectNode.set("rules", rulesNode);
                        return objectNode;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparingInt(o -> o.get("awardSort").asInt()))
                .toList();
    }

    @Override
    public Integer getAward(Integer userId, Integer strategyId) {
        // 执行过滤器链
        FilterParam filterParam = raffleFilterChain.doFilter(
                FilterParam.builder()
                        .UserId(userId)
                        .StrategyId(strategyId)
                        .build()
        );

        log.atInfo().log("调度了: {}", filterParam.getDispatchParam());
        log.atInfo().log("抽到了: {}", filterParam.getAwardId());

        return filterParam.getAwardId();
    }

}
