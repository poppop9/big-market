package app.xlog.ggbond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

/**
 * 返利领域 api
 */
public interface IRewardApiService {

    // 查询用户积分
    ResponseEntity<JsonNode> findUserRewardAccountPoints(Long activityId);

}
