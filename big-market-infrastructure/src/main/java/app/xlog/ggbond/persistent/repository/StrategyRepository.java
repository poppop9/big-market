package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.po.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    @Query("select s from Strategy s where s.strategyId = ?1")
    Strategy findByStrategyId(Integer strategyId);
}
