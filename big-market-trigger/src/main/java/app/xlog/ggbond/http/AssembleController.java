package app.xlog.ggbond.http;

import app.xlog.ggbond.IAssembleApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.service.armory.IRaffleArmory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        strategyArmory.assembleLotteryStrategyRuleCommon(10001);
        log.atInfo().log("装配策略10001的全奖品完成");

        strategyArmory.assembleLotteryStrategyRuleLock(10001);
        log.atInfo().log("装配策略10001的除锁定奖品完成");

        strategyArmory.assembleLotteryStrategyRuleLockLong(10001);
        log.atInfo().log("装配策略10001的除最后一个奖品完成");

        strategyArmory.assembleLotteryStrategyRuleGrand(10001);
        log.atInfo().log("装配策略10001的大奖池完成");

        strategyArmory.assembleLotteryStrategyAwardCount(10001);
        log.atInfo().log("装配策略10001的奖品库存完成");

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree("装配完成"))
                .build();
    }
}
