package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleDispatchApiService;
import app.xlog.ggbond.model.Response;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffle.model.bo.UserRaffleHistoryBO;
import app.xlog.ggbond.raffle.service.IRaffleArmory;
import app.xlog.ggbond.raffle.service.IRaffleDispatch;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.service.ISecurityService;
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
import java.util.*;

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
     * 查询对应的奖品列表  todo 大改，所有层级要加上activity
     **/
    @Override
    @GetMapping("/v2/queryAwardList")
    public Response<JsonNode> queryAwardList(@RequestParam Long activityId) {
        // 自动获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        List<AwardBO> awardBOs = raffleArmory.findAllAwards(activityId, user.getUserId());

        log.atDebug().log("查询了活动 {} ，用户 {} 的奖品列表", activityId, user.getUserId());
        return Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardBOs))
                .build();
    }

    /**
     * 抽取奖品
     **/
    @Override
    @GetMapping("/v2/getAward")
    public Response<JsonNode> getAward(@RequestParam Long activityId) {
        // 自动获取当前用户
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        Long awardId = raffleDispatch.getAwardId(activityId, user.getUserId());

        log.atInfo().log(
                "抽奖领域 - " + securityService.getLoginIdDefaultNull() + " 抽到 {} 活动的 {} 奖品", activityId, awardId
        );

        return awardId != null
                ? Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用成功")
                .data(objectMapper.valueToTree(awardId))
                .build()
                : Response.<JsonNode>builder()
                .status(HttpStatus.OK)
                .info("调用失败")
                .data(objectMapper.valueToTree(null))
                .build();
    }

    /**
     * 实时获取中奖奖品信息
     */
    @Override
    @GetMapping(value = "/v1/getWinningAwardsInfo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Response<JsonNode>> getWinningAwardsInfo(@RequestParam Long activityId) {
        // 自动获取当前用户的最新的策略id
        UserBO user = securityService.findUserByUserId(securityService.getLoginIdDefaultNull());
        List<UserRaffleHistoryBO> winningAwards = raffleArmory.findWinningAwardsInfo(activityId, user.getUserId());

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
