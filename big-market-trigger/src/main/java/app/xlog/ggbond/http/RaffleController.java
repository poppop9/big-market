package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.raffle.service.IRaffleDispatch;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 抽奖领域 - 抽奖
 */
@Slf4j
@RestController
@RequestMapping("/api/raffle/award")
public class RaffleController implements IRaffleApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IRaffleArmory raffleArmory;
    @Resource
    private IRaffleDispatch raffleDispatch;

    /**
     * 根据策略id，查询对应的奖品列表
     **/
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
     * 根据策略id，抽取奖品
     **/
    @Override
    @GetMapping("/v1/getAward")
    public Response<JsonNode> getAward(@RequestParam Long userId, @RequestParam Long strategyId) {
        Long awardId = raffleDispatch.getAwardByStrategyId(userId, strategyId);
        log.atInfo().log("抽奖领域 - 抽到 {} 策略的 {} 奖品", strategyId, awardId);

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardId))
                .build();
    }

}
