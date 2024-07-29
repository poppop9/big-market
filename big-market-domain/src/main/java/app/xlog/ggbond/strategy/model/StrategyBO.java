package app.xlog.ggbond.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyBO {
    private Integer strategyId;
    private String rules;
}
