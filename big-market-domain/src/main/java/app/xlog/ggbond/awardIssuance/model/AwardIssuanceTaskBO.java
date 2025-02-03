package app.xlog.ggbond.awardIssuance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品发放任务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardIssuanceTaskBO {
    private Long awardIssuanceId;  // 奖品发放任务id
    private Long userId;  // 用户id
    private Long userRaffleHistoryId;  // 用户抽奖历史id
    private Boolean isIssued;  // 奖品是否发放
}
