package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.service.IRaffleService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/award")
public class RaffleController implements IRaffleApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IRaffleService raffleService;

    /**
     * 根据策略id，查询奖品列表
     **/
    @Override
    @GetMapping("/v1/queryAwardList")
    public Response<JsonNode> queryAwardList(@RequestParam Long strategyId) {
        List<ObjectNode> awardBOs = raffleService.queryAwardList(strategyId);

        log.atInfo().log("查询了策略 {} 的奖品列表", strategyId);
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
        Long awardId = raffleService.getAward(userId, strategyId);
        log.atInfo().log("抽奖领域 - 抽到 {} 奖品", awardId);

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardId))
                .build();
    }
}
