package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.reward.ExchangePrizes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA - 兑换奖品
 */
@Repository
public interface ExchangePrizesJpa extends JpaRepository<ExchangePrizes, Long> {
    @Query("select e from ExchangePrizes e where e.activityId = ?1")
    List<ExchangePrizes> findByActivityId(Long activityId);
}
