package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA - 活动仓储
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
