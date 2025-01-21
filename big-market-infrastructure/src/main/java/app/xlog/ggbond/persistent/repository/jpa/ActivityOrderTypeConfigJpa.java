package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrderTypeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA - 活动单类型配置
 */
@Repository
public interface ActivityOrderTypeConfigJpa extends JpaRepository<ActivityOrderTypeConfig, Long> {
    @Query("select a from ActivityOrderTypeConfig a where a.activityId = ?1")
    List<ActivityOrderTypeConfig> findByActivityId(Long activityId);
}
