package app.xlog.ggbond.persistent.po.raffle;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 抽奖规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RaffleRule")
public class RaffleRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RuleType ruleType;  // 规则类型
    private Long strategyOrAwardId;  // 策略id或奖品id，由ruleType决定
    @Enumerated(EnumType.STRING)
    private RuleKey ruleKey;  // 规则名称
    private Long ruleValue;  // 规则值，为-1表示该规则无需值
    private Integer ruleGrade;  // 规则级别(0为最高级)，当同一个奖品有多个规则时，优先匹配哪个
    private String ruleDescription;  // 规则描述
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();


    @Getter
    @AllArgsConstructor
    public enum RuleType {
        Strategy("Strategy"),  // 其实策略规则，也就是调度规则
        Award("Award");  // 其实奖品规则，也就是限制规则

        private final String value;
    }

    @Getter
    @AllArgsConstructor
    public enum RuleKey {
        rule_common("rule_common"),
        rule_lock("rule_lock"),
        rule_grand("rule_grand");

        private final String value;
    }
}
