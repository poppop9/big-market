package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.StrategyAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JPA - 策略·奖品中间表仓储
 */
@Repository
public interface StrategyAwardJpa extends JpaRepository<StrategyAward, Long> {

    @Query("select s from StrategyAward s where s.strategyId = ?1 order by s.awardSort")
    List<StrategyAward> findByStrategyIdOrderByAwardSortAsc(Long strategyId);

    @Modifying
    @Transactional
    @Query("UPDATE StrategyAward s " +
            "SET s.awardCount = s.awardCount - 1 " +
            "WHERE s.strategyId = :strategyId " +
            "AND s.awardId = :awardId")
    void decrementAwardCountByStrategyIdAndAwardId(Long strategyId, Long awardId);

}
