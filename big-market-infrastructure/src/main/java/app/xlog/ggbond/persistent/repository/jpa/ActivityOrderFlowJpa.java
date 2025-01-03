package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrderFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA - 活动单流水仓储
 */
@Repository
public interface ActivityOrderFlowJpa extends JpaRepository<ActivityOrderFlow, Long> {
}
