package app.xlog.ggbond.raffle.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DecrQueueVO {
    private Long strategyId;
    private Long awardId;
}
