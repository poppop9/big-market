package app.xlog.ggbond.raffle.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwardBO {
    private Long awardId;  // 奖品id
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    private Long awardCount;  // 奖品库存
    private Integer awardSort;  // 奖品在前端的排序
}
