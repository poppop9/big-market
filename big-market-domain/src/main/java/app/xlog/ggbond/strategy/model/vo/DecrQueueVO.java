package app.xlog.ggbond.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DecrQueueVO {
    private Integer strategyId;
    private Integer awardId;
}