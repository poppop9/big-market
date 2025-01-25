package app.xlog.ggbond.http;

import app.xlog.ggbond.IActivityApiService;
import app.xlog.ggbond.resp.ZakiResponse;
import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.integrationService.TriggerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 活动领域 - 充值活动单
     */
    @PostMapping("/v1/rechargeAO")
    public ResponseEntity<JsonNode> rechargeAO(@RequestBody AOContext aoContext) {
        aoContext = triggerService.rechargeAO(aoContext);
        return ZakiResponse.ok("activityOrderBO", aoContext.getActivityOrderBO());
    }

    /**
     * 活动领域 - 查询所有待支付的活动单
     */
    @GetMapping("/v1/findAllPendingPaymentAO")
    public ResponseEntity<JsonNode> findAllPendingPaymentAO(@RequestParam Long activityId) {
        List<ActivityOrderBO> activityOrderBOList = triggerService.findAllPendingPaymentAO(activityId);
        return ZakiResponse.ok("activityOrderBOList", activityOrderBOList);
    }

    /**
     * 活动领域 - 支付待支付的活动单
     */
    @PatchMapping("/v1/payPendingPaymentAO")
    public ResponseEntity<JsonNode> payPendingPaymentAO(@RequestBody AOContext aoContext) {
        aoContext = triggerService.payPendingPaymentAO(aoContext);
        return ZakiResponse.ok("activityOrderBO", aoContext.getActivityOrderBO());
    }

}
