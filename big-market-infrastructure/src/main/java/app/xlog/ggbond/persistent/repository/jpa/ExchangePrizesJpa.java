package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.reward.ExchangePrizes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA - 兑换奖品
 */
public interface ExchangePrizesJpa extends JpaRepository<ExchangePrizes, Long> {
}
