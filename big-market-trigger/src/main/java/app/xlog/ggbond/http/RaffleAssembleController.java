package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleAssembleApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffleAndSecurity.RaffleSecurityAppService;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

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
    private RaffleSecurityAppService raffleSecurityAppService;

    /**
     * 查询对应的奖品列表
     **/
    @Override
    @GetMapping("/v2/queryAwardList")
    public Response<JsonNode> queryAwardList(@RequestParam Long activityId) {
        List<AwardBO> awardBOs = raffleSecurityAppService.findAllAwardsByActivityIdAndCurrentUser(activityId);

        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardBOs))
                .build();
    }

    /**
     * 实时获取中奖奖品信息
     */
    @Override
    @GetMapping(value = "/v1/getWinningAwardsInfo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Response<JsonNode>> getWinningAwardsInfo(@RequestParam Long activityId) {
        List<UserRaffleHistoryBO> winningAwards = raffleSecurityAppService.findAllWinningAwards(activityId);

        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(sequence -> Mono
                        .fromCallable(() -> Response.<JsonNode>builder()
                                .status(HttpStatus.OK)
                                .info("调用成功")
                                .data(objectMapper.valueToTree(winningAwards))
                                .build())
                        .onErrorReturn(Response.<JsonNode>builder()
                                .status(HttpStatus.OK)
                                .info("调用成功")
                                .data(objectMapper.valueToTree("奖品2"))
                                .build())
                );
    }

}
