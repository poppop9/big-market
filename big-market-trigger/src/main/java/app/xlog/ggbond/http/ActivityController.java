package app.xlog.ggbond.http;

import app.xlog.ggbond.IActivityApiService;
import app.xlog.ggbond.ZakiResponse;
import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.integrationService.TriggerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
