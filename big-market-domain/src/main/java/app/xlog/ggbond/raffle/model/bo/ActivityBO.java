package app.xlog.ggbond.raffle.model.bo;

import app.xlog.ggbond.GlobalConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动
 */
@Data
public class ActivityBO {
    private Long activityId;  // 活动id
    private List<Long> strategyIdList = new ArrayList<>(List.of(GlobalConstant.defaultStrategyId));  // 该活动下的所有的策略id
}
