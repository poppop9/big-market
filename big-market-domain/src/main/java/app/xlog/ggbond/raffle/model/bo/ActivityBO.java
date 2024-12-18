package app.xlog.ggbond.raffle.model.bo;

import lombok.Data;

import java.util.List;

/**
 * 活动
 */
@Data
public class ActivityBO {
    private Long activityId;  // 活动id
    private Long defaultStrategyId;  // 默认策略id（每个活动都会有一个默认的策略id）
    private List<Long> strategyIdList;  // 该活动下的所有的策略id
}
