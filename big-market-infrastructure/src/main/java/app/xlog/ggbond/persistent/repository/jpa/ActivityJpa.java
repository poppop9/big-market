package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA - 活动仓储
 */
@Repository
public interface ActivityJpa extends JpaRepository<Activity, Long> {

}
