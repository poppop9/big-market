package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleAssembleApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.service.IRaffleArmory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抽奖领域 - 装配接口
 **/
@Slf4j
@RestController
@RequestMapping("/api/raffle/assemble")
public class RaffleAssembleController implements IRaffleAssembleApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IRaffleArmory strategyArmory;

    /**
     * 抽奖前的装配准备
     */
    @Override
    @GetMapping("/v1/assembleRaffleAll")
    public Response<JsonNode> assembleRaffleAll(@RequestParam Long strategyId) {
        // 装配该策略所需的所有权重对象
        strategyArmory.assembleRaffleWeightRandomByStrategyId(strategyId);
        // 装配该策略所需的所有奖品的库存
        strategyArmory.assembleAllAwardCountBystrategyId(strategyId);

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree("装配完成"))
                .build();
    }

}
