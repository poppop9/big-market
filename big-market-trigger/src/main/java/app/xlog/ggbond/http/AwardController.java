package app.xlog.ggbond.http;

import app.xlog.ggbond.strategy.model.vo.FilterParam;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import app.xlog.ggbond.strategy.service.filter.RaffleFilterChain;
import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@CrossOrigin("${app.config.CrossOrigin}")
@RestController
@RequestMapping("/api/award")
public class AwardController {

    private static final Logger log = LoggerFactory.getLogger(AwardController.class);
    @Resource
    private IStrategyRepository strategyRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RaffleFilterChain raffleFilterChain;

    /**
     * 根据策略id，查询奖品列表
     **/
    @GetMapping("/queryAwardList")
    public ResponseEntity<ObjectNode> queryAwardList(@RequestParam Integer strategyId) throws Exception {
        Assert.notNull(strategyId, "策略id不存在");

        List<ObjectNode> awardBOS =
                strategyRepository.queryCommonAwards(strategyId).stream()
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

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.set("AwardList", objectMapper.valueToTree(awardBOS));

        log.atInfo().log("查询了策略{}的奖品列表", strategyId);
        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
    }

    /**
     * 根据策略id，抽取奖品
     **/
    @GetMapping("/getAward")
    public ResponseEntity<ObjectNode> getAward(@RequestParam Integer userId,
                                               @RequestParam Integer strategyId) {
        Assert.notNull(userId, "用户id不存在");
        Assert.notNull(strategyId, "策略id不存在");

        // 执行过滤器链
        FilterParam filterParam = raffleFilterChain.doFilter(
                FilterParam.builder()
                        .UserId(userId)
                        .StrategyId(strategyId)
                        .build()
        );

        log.atInfo().log("调度了: {}", filterParam.getDispatchParam());
        log.atInfo().log("抽到了: {}", filterParam.getAwardId());

        ObjectNode objectNode = objectMapper.createObjectNode()
                .put("awardId", filterParam.getAwardId());
        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
    }
}
