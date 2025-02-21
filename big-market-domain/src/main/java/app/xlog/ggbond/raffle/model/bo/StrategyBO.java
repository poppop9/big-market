package app.xlog.ggbond.raffle.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 策略
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyBO implements Serializable {
    private Long strategyId;
}
