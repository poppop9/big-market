package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

/**
 * 返利领域 api
 */
public interface IRewardApiService {

    // 查询用户积分
    ResponseEntity<JsonNode> findUserRewardAccountPoints(Long activityId);

    // 查询兑换奖品
    ResponseEntity<JsonNode> findExchangePrizes(Long activityId);

    // 兑换奖品
    ResponseEntity<JsonNode> exchangePrizes(Long activityId, Long exchangePrizesId);

    // 查询兑换奖品历史
    ResponseEntity<JsonNode> findExchangePrizesLogList(Long activityId);

}
