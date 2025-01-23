package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 活动领域 api
 */
public interface IActivityApiService {

    // 活动领域 - 充值活动单
    ResponseEntity<JsonNode> rechargePendingPaymentAO( Long activityId, String activityOrderTypeName);

}
