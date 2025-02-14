package app.xlog.ggbond.raffle.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 奖品
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AwardBO {
    private Long awardId;  // 奖品id
    private String awardIdStr;  // 奖品id字符串
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
    private Double awardRate;  // 奖品被抽取到的概率，单位是%
    private Long awardCount;  // 奖品库存
    private Integer awardSort;  // 奖品在前端的排序

    public final static AwardBO randomPointsAward = AwardBO.builder()
            .awardId(101L)
            .awardTitle("随机积分")
            .awardRate(74.0)
            .awardCount(8000L)
            .awardSort(1)
            .build();

}
