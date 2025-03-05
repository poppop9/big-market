package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JPA - 活动仓储
 */
@Repository
public interface ActivityJpa extends JpaRepository<Activity, Long> {

    @Query("select a from Activity a where a.activityId = ?1")
    Activity findByActivityId(Long activityId);
}
