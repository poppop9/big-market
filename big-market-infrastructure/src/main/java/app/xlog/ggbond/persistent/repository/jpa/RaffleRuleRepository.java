package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.RaffleRule;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaffleRuleRepository extends JpaRepository<RaffleRule, Long> {

    List<RaffleRule> findByRuleTypeAndStrategyOrAwardIdOrderByRuleGradeAsc(RaffleRule.RuleType ruleType, Long strategyOrAwardId, Sort sort);

}
