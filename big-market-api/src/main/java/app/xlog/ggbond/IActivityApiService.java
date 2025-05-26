package app.xlog.ggbond;

import app.xlog.ggbond.activity.model.vo.AOContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

/**
 * 活动领域 api
 */
public interface IActivityApiService {

    // 充值活动单
    ResponseEntity<JsonNode> rechargeAO(AOContext aoContext);

    // 查询所有待支付的活动单
    ResponseEntity<JsonNode> findAllPendingPaymentAO(Long activityId);

    // 支付待支付的活动单
    ResponseEntity<JsonNode> payPendingPaymentAO(Long activityId, Long activityOrderId);

    // 用户取消活动单
    ResponseEntity<JsonNode> cancelAO(String activityOrderId);

    // 查询用户的可用抽奖次数
    ResponseEntity<JsonNode> findAvailableRaffleCount(Long activityId);

}
