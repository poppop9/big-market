package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrder;
import app.xlog.ggbond.persistent.po.activity.ActivityOrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA - 活动单仓储
 */
@Repository
public interface ActivityOrderJpa extends JpaRepository<ActivityOrder, Long> {

    @Query("""
            select (count(a) > 0) from ActivityOrder a
            where a.userId = ?1 and a.activityId = ?2 and a.activityOrderTypeName = ?3 and a.activityOrderStatus = ?4 and a.createTime between ?5 and ?6""")
    boolean existsByUserIdAndActivityIdAndActivityOrderTypeNameAndActivityOrderStatusAndCreateTimeBetween(Long userId, Long activityId, ActivityOrderType.ActivityOrderTypeName activityOrderTypeName, ActivityOrder.ActivityOrderStatus activityOrderStatus, LocalDateTime createTimeStart, LocalDateTime createTimeEnd);

    @Transactional
    @Modifying
    @Query("update ActivityOrder a set a.activityOrderStatus = ?1 where a.activityOrderId = ?2")
    int updateActivityOrderStatusByActivityOrderId(ActivityOrder.ActivityOrderStatus activityOrderStatus, Long activityOrderId);

    @Query("""
            select a from ActivityOrder a
            where a.activityId = ?1 and a.userId = ?2 and a.activityOrderStatus = ?3
            order by a.createTime""")
    List<ActivityOrder> findByActivityIdAndUserIdAndActivityOrderStatusOrderByCreateTimeAsc(Long activityId, Long userId, ActivityOrder.ActivityOrderStatus activityOrderStatus);

    @Query("select a from ActivityOrder a where a.activityOrderId = ?1")
    ActivityOrder findByActivityOrderId(Long activityOrderId);
}
