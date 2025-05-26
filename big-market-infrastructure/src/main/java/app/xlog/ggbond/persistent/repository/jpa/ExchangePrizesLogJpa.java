package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.reward.ExchangePrizes;
import app.xlog.ggbond.persistent.po.reward.ExchangePrizesLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA - 兑换奖品流水
 */
@Repository
public interface ExchangePrizesLogJpa extends JpaRepository<ExchangePrizesLog, Long> {
    @Query("select e from ExchangePrizesLog e where e.activityId = ?1 and e.userId = ?2")
    List<ExchangePrizesLog> findByActivityIdAndUserId(Long activityId, Long userId);
}
