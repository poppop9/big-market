package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaffleRuleRepository extends JpaRepository<RafflePool, Long> {

    @Query("select r from RafflePool r where r.strategyId = ?1 order by r.ruleGrade")
    List<RafflePool> findByStrategyIdOrderByRuleGradeAsc(Long strategyId, Sort sort);

}
