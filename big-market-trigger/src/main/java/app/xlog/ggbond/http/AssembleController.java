package app.xlog.ggbond.http;

import app.xlog.ggbond.IAssembleApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.service.armory.IRaffleArmory;
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
 * 装配奖品池
 **/
@Slf4j
@RestController
@RequestMapping("/api/assemble")
public class AssembleController implements IAssembleApiService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IRaffleArmory strategyArmory;

    @Override
    @GetMapping("/v1/assembleRaffleAll")
    public Response<JsonNode> assembleRaffleAll(@RequestParam Integer strategyId) {
        strategyArmory.assembleLotteryStrategyRuleCommon(10001L);
        strategyArmory.assembleLotteryStrategyRuleLock(10001L);
        strategyArmory.assembleLotteryStrategyRuleLockLong(10001L);
        strategyArmory.assembleLotteryStrategyRuleGrand(10001L);
        strategyArmory.assembleLotteryStrategyAwardCount(10001L);

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree("装配完成"))
                .build();
    }
}
