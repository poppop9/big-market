package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA - 抽奖池仓储
 */
@Repository
public interface RafflePoolRepository extends JpaRepository<RafflePool, Long> {

    @Query("select r from RafflePool r where r.strategyId = ?1")
    List<RafflePool> findByStrategyId(Long strategyId);

}