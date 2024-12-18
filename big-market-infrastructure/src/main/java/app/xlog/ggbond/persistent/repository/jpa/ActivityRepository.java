package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * JPA - 活动仓储
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("select a from Activity a where a.activityId = ?1")
    Activity findByActivityId(Long activityId);

}
