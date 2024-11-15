package app.xlog.ggbond.raffle.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 抽奖规则业务对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaffleRuleBO {
    private RuleType ruleType;  // 规则类型
    private Long strategyOrAwardId;  // 策略id或奖品id，由ruleType决定
    private RuleKey ruleKey;  // 规则名称
    private Long ruleValue;  // 规则值，为-1表示该规则无需值
    private Integer ruleGrade;  // 规则级别，当同一个奖品有多个规则时，优先匹配哪个


    @Getter
    @AllArgsConstructor
    public enum RuleType {
        Strategy("Strategy"),
        Award("Award");

        private final String value;
    }

    @Getter
    @AllArgsConstructor
    public enum RuleKey {
        // 策略规则
        rule_grand("rule_grand"),
        // 奖品规则
        rule_common("rule_common"),
        rule_lock("rule_lock");

        private final String value;
    }
}
