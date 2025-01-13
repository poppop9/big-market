package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JPA - 策略仓储
 */
@Repository
public interface StrategyJpa extends JpaRepository<Strategy, Long> {

    @Query("select s from Strategy s where s.strategyId = ?1")
    Strategy findByStrategyId(Long strategyId);

}
