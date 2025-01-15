package app.xlog.ggbond.raffle.model.bo;

import lombok.Data;

import java.util.List;

/**
 * 活动
 */
@Data
public class ActivityBO {
    private Long activityId;  // 活动id
    private String activityName;  // 活动名称
    private List<Long> strategyIdList;  // 该活动下的所有的策略id
}
