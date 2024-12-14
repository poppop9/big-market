package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleDispatchApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.raffle.service.IRaffleDispatch;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.service.ISecurityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 抽奖领域 - 调度接口
 */
@Slf4j
@RestController
@RequestMapping("/api/raffle/dispatch")
public class RaffleDispatchController implements IRaffleDispatchApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IRaffleArmory raffleArmory;
    @Resource
    private IRaffleDispatch raffleDispatch;
    @Resource
    private ISecurityService securityService;

    /**
     * 弃用 - 根据策略id，查询对应的奖品列表
     **/
    @Deprecated
    @Override
    @GetMapping("/v1/queryAwardList")
    public Response<JsonNode> queryAwardList(@RequestParam Long strategyId) {
        List<ObjectNode> awardBOs = raffleArmory.findAllAwardByStrategyId(strategyId);

        log.atDebug().log("查询了策略 {} 的奖品列表", strategyId);
        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardBOs))
                .build();
    }

    /**
     * 查询对应的奖品列表
     **/
    @Override
    @GetMapping("/v2/queryAwardList")
    public Response<JsonNode> queryAwardList() {
        // 自动获取当前用户的最新的策略id
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        LinkedHashMap<Long, Long> strategyRaffleTimeMap = user.getStrategyRaffleTimeMap();
        String strategyId = strategyRaffleTimeMap.keySet().toArray()[strategyRaffleTimeMap.size() - 1].toString();

        // 查询奖品列表
        List<ObjectNode> awardBOs = raffleArmory.findAllAwardByStrategyId(Long.valueOf(strategyId));

        log.atDebug().log("查询了策略 {} 的奖品列表", strategyId);
        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardBOs))
                .build();
    }

    /**
     * 根据策略id，抽取奖品
     **/
    @Deprecated
    @Override
    @GetMapping("/v1/getAward")
    public Response<JsonNode> getAward(@RequestParam Long strategyId) {
        Long awardId = raffleDispatch.getAwardByStrategyId(strategyId);
        Long userId = securityService.getLoginIdDefaultNull();
        log.atInfo().log(
                "抽奖领域 - " + (userId == null ? "游客" : userId) + " 抽到 {} 策略的 {} 奖品", strategyId, awardId
        );

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardId))
                .build();
    }

    /**
     * 抽取奖品
     **/
    @Override
    @GetMapping("/v2/getAward")
    public Response<JsonNode> getAward() {
        // 自动获取当前用户的最新的策略id
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        LinkedHashMap<Long, Long> strategyRaffleTimeMap = user.getStrategyRaffleTimeMap();
        String strategyId = strategyRaffleTimeMap.keySet().toArray()[strategyRaffleTimeMap.size() - 1].toString();

        // 抽取奖品
        Long awardId = raffleDispatch.getAwardByStrategyId(Long.valueOf(strategyId));
        Long userId = securityService.getLoginIdDefaultNull();
        log.atInfo().log(
                "抽奖领域 - " + (userId == null ? "游客" : userId) + " 抽到 {} 策略的 {} 奖品", strategyId, awardId
        );

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardId))
                .build();
    }

    /**
     * 实时获取中奖奖品信息 todo
     */
    @GetMapping(value = "/v1/getWinningAwardsInfo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Response<JsonNode>> getWinningAwardsInfo() {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(sequence -> Mono
                        .fromCallable(() -> Response.<JsonNode>builder()
                                .status(HttpStatus.OK)
                                .info("调用成功")
                                .data(objectMapper.valueToTree("奖品1"))
                                .build())
                        .onErrorReturn(Response.<JsonNode>builder()
                                .status(HttpStatus.OK)
                                .info("调用成功")
                                .data(objectMapper.valueToTree("奖品2"))
                                .build())
                );
    }

}
