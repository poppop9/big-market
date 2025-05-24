package app.xlog.ggbond.http;

import app.xlog.ggbond.IActivityApiService;
import app.xlog.ggbond.activity.service.ActivityService;
import app.xlog.ggbond.activity.service.IActivityService;
import app.xlog.ggbond.activity.service.statusFlow.AOEventCenter;
import app.xlog.ggbond.resp.ZakiResponse;
import app.xlog.ggbond.activity.model.bo.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.integrationService.TriggerService;
import app.xlog.ggbond.security.service.ISecurityService;
import app.xlog.ggbond.security.service.SecurityService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动领域
 */
@Slf4j
@RestController
@RequestMapping("/api/activity")
public class ActivityController implements IActivityApiService {

    @Resource
    private TriggerService triggerService;
    @Resource
    private AOEventCenter aoEventCenter;
    @Resource
    private IActivityService activityService;
    @Resource
    private ISecurityService securityService;

    /**
     * 充值活动单
     */
    @PostMapping("/v1/rechargeAO")
    public ResponseEntity<JsonNode> rechargeAO(@RequestBody AOContext aoContext) {
        aoContext = triggerService.rechargeAO(aoContext);
        return ZakiResponse.ok("activityOrderBO", aoContext.getActivityOrderBO());
    }

    /**
     * 查询所有待支付的活动单
     */
    @GetMapping("/v1/findAllPendingPaymentAO")
    public ResponseEntity<JsonNode> findAllPendingPaymentAO(@RequestParam Long activityId) {
        List<ActivityOrderBO> activityOrderBOList = triggerService.findAllPendingPaymentAO(activityId);
        return ZakiResponse.ok("activityOrderBOList", activityOrderBOList);
    }

    /**
     * 支付待支付的活动单
     */
    @PatchMapping("/v1/payPendingPaymentAO")
    public ResponseEntity<JsonNode> payPendingPaymentAO(@RequestBody AOContext aoContext) {
        aoContext = aoEventCenter.publishPendingPaymentToEffectiveEvent(aoContext);
        return ZakiResponse.ok("activityOrderBO", aoContext.getActivityOrderBO());
    }

    /**
     * 用户取消活动单
     */
    @Override
    @PatchMapping("/v1/cancelAO")
    public ResponseEntity<JsonNode> cancelAO(@RequestParam String activityOrderId) {
        aoEventCenter.publishPendingPaymentToClosedEvent(AOContext.builder()
                .activityOrderBO(ActivityOrderBO.builder()
                        .activityOrderId(Long.valueOf(activityOrderId))
                        .build())
                .build());
        return ZakiResponse.ok("取消活动单成功");
    }

    /**
     * 查询用户的可用抽奖次数
     */
    @Override
    @GetMapping("/v1/findAvailableRaffleCount")
    public ResponseEntity<JsonNode> findAvailableRaffleCount(Long activityId) {
        Long userId = securityService.getLoginIdDefaultNull();
        Long availableRaffleCount = activityService.findAvailableRaffleCount(userId, activityId);
        return ZakiResponse.ok("availableRaffleCount", availableRaffleCount);
    }

}
