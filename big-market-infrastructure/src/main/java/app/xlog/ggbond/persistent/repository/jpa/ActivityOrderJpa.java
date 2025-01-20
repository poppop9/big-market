package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA - 活动单流水仓储
 */
@Repository
public interface ActivityOrderJpa extends JpaRepository<ActivityOrder, Long> {
}
