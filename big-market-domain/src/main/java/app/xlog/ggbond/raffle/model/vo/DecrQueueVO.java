package app.xlog.ggbond.raffle.model.vo;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecrQueueVO {
    private Long strategyId;
    private Long awardId;
}
