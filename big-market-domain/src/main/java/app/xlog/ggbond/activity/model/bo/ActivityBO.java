package app.xlog.ggbond.activity.model.bo;

import lombok.Data;

/**
 * 活动
 */
@Data
public class ActivityBO {
    private Long activityId;  // 活动id
    private String activityName;  // 活动名称
    private String rangeOfPoints; // 积分范围（格式："1-10"），随机积分奖品就可能是1-10之间的随机数
}