package app.xlog.ggbond.http;

import app.xlog.ggbond.IActivityApiService;
import app.xlog.ggbond.ZakiResponse;
import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.integrationService.TriggerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 活动领域 - 充值待支付活动单
     *
     * @param activityId            活动id
     * @param activityOrderTypeName 活动单类型名称
     */
    @PostMapping("/v1/rechargePendingPaymentAO")
    public ResponseEntity<JsonNode> rechargePendingPaymentAO(@RequestParam Long activityId,
                                                             @RequestParam String activityOrderTypeName) {
        ActivityOrderBO activityOrderBO = triggerService.rechargePendingPaymentAO(activityId, activityOrderTypeName);
        return ZakiResponse.ok("activityOrderBO", activityOrderBO);
    }

}
