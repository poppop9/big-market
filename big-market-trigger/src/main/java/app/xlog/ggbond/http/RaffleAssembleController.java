package app.xlog.ggbond.http;

import app.xlog.ggbond.IRaffleAssembleApiService;
import app.xlog.ggbond.ZakiResponse;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.raffleAndSecurity.RaffleSecurityAppService;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * 抽奖领域 - 装配
 **/
@Slf4j
@RestController
@RequestMapping("/api/raffle/assemble")
public class RaffleAssembleController implements IRaffleAssembleApiService {

    @Resource
    private RaffleSecurityAppService raffleSecurityAppService;

    /**
     * 查询对应的奖品列表
     */
    @Override
    @GetMapping("/v2/queryAwardList")
    public ResponseEntity<JsonNode> queryAwardList(@RequestParam Long activityId) {
        List<AwardBO> awardBOs = raffleSecurityAppService.findAllAwardsByActivityIdAndCurrentUser(activityId);
        return ZakiResponse.ok("awardBOs", awardBOs);
    }

    /**
     * 实时获取中奖奖品信息
     */
    @Override
    @SneakyThrows
    @GetMapping(value = "/v1/getWinningAwardsInfo")
    public SseEmitter getWinningAwardsInfo(@RequestParam Long activityId) {
        List<UserRaffleHistoryBO> winningAwards = raffleSecurityAppService.findAllWinningAwards(activityId);

        // 创建一个 SseEmitter 对象，设置超时时间为10秒
        SseEmitter emitter = new SseEmitter(10_000L);

        // 在新的线程中发送事件
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    // 向客户端发送消息
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data("Hello, this is message " + i)
                            .id(String.valueOf(i))
                            .reconnectTime(1000));
                    // 模拟延时
                    Thread.sleep(1000);
                }
                emitter.complete();  // 完成 SSE 连接
            } catch (Exception e) {
                emitter.completeWithError(e);  // 如果发生异常，则终止连接
            }
        }).start();

        return emitter;
    }

}
