package app.xlog.ggbond.raffle.service;

import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import app.xlog.ggbond.raffle.service.filterChain.RaffleFilterChain;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class RaffleService implements IRaffleService {

    @Resource
    private IRaffleRepository raffleRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RaffleFilterChain raffleFilterChain;

    @Override
    public List<ObjectNode> queryAwardList(Long strategyId) {
        return raffleRepository.queryCommonAwards(strategyId).stream()
                .map(awardBO -> objectMapper.<ObjectNode>valueToTree(awardBO))
                .sorted(Comparator.comparingInt(o -> o.get("awardSort").asInt()))
                .toList();
    }

    @Override
    public Long getAward(Long userId, Long strategyId) {
        // 执行过滤器链
        return raffleFilterChain.executeFilterChain(RaffleFilterContext.builder()
                .userId(userId)
                .strategyId(strategyId)
                .middleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS)
                .build()
        );
    }
}
