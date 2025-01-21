package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA - 活动单类型仓储
 */
@Repository
public interface ActivityOrderTypeJpa extends JpaRepository<ActivityOrderType, Long> {
}
