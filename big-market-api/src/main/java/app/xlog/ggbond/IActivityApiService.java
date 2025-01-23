package app.xlog.ggbond;

import app.xlog.ggbond.activity.model.vo.AOContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

/**
 * 活动领域 api
 */
public interface IActivityApiService {

    // 活动领域 - 充值活动单
    ResponseEntity<JsonNode> rechargeAO(AOContext aoContext);

}
