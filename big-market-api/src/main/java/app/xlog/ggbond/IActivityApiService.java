package app.xlog.ggbond;

import app.xlog.ggbond.activity.model.vo.AOContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 活动领域 api
 */
public interface IActivityApiService {

    // 活动领域 - 充值活动单
    ResponseEntity<JsonNode> rechargeAO(AOContext aoContext);

    // 活动领域 - 查询所有待支付的活动单
    ResponseEntity<JsonNode> findAllPendingPaymentAO(Long activityId);

    // 活动领域 - 支付待支付的活动单
    ResponseEntity<JsonNode> payPendingPaymentAO(AOContext aoContext);

}
