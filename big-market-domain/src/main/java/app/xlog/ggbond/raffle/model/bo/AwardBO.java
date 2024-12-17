package app.xlog.ggbond.raffle.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardBO {
    private Long strategyId;  // 策略id
    private Long awardId;  // 奖品id
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
    private Long awardCount;  // 奖品库存
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    private Integer awardSort;  // 奖品在前端的排序
}
