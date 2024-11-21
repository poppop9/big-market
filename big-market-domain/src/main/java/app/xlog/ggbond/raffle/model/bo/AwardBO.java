package app.xlog.ggbond.raffle.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
奖品业务对象 - 用于业务逻辑处理，所以简化了一些属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardBO {
    private Long strategyId;
    private Long awardId;
    private String awardTitle;
    private String awardSubtitle;
    private Long awardCount;
    private Double awardRate;
    private Integer awardSort;
}
