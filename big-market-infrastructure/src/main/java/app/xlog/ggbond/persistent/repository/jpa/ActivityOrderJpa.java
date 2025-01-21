package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrder;
import app.xlog.ggbond.persistent.po.activity.ActivityOrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JPA - 活动单仓储
 */
@Repository
public interface ActivityOrderJpa extends JpaRepository<ActivityOrder, Long> {
    @Query("""
            select (count(a) > 0) from ActivityOrder a
            where a.userId = ?1 and a.activityId = ?2 and a.activityOrderTypeName = ?3""")
    boolean existsByUserIdAndActivityIdAndActivityOrderTypeName(Long userId, Long activityId, ActivityOrderType.ActivityOrderTypeName activityOrderTypeName);
}
