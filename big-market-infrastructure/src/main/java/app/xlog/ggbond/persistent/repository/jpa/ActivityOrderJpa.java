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
import java.util.Optional;

/**
 * JPA - 活动单仓储
 */
@Repository
public interface ActivityOrderJpa extends JpaRepository<ActivityOrder, Long> {

    @Transactional
    @Modifying
    @Query("update ActivityOrder a set a.activityOrderStatus = ?1 where a.activityOrderId = ?2")
    void updateActivityOrderStatusByActivityOrderId(ActivityOrder.ActivityOrderStatus activityOrderStatus, Long activityOrderId);

    @Query("""
            select a from ActivityOrder a
            where a.activityId = ?1 and a.userId = ?2 and a.activityOrderStatus = ?3
            order by a.createTime""")
    List<ActivityOrder> findByActivityIdAndUserIdAndActivityOrderStatusOrderByCreateTimeAsc(Long activityId, Long userId, ActivityOrder.ActivityOrderStatus activityOrderStatus);

    @Query("select a from ActivityOrder a where a.activityOrderId = ?1")
    ActivityOrder findByActivityOrderId(Long activityOrderId);

    @Transactional
    @Modifying
    @Query("""
            update ActivityOrder a set a.activityOrderStatus = ?1, a.activityOrderTypeId = ?2
            where a.activityOrderId = ?3""")
    void updateActivityOrderStatusAndActivityOrderTypeIdByActivityOrderId(ActivityOrder.ActivityOrderStatus activityOrderStatus, Long activityOrderTypeId, Long activityOrderId);

    @Transactional
    @Modifying
    @Query("""
            update ActivityOrder a set a.activityOrderStatus = ?1, a.activityOrderTypeId = ?2, a.totalRaffleCount = ?3
            where a.activityOrderId = ?4""")
    void updateActivityOrderStatusAndActivityOrderTypeIdAndTotalRaffleCountByActivityOrderId(ActivityOrder.ActivityOrderStatus activityOrderStatus, Long activityOrderTypeId, Long totalRaffleCount, Long activityOrderId);

    @Query("select a from ActivityOrder a where a.activityId = ?1 and a.userId = ?2 and a.activityOrderStatus = ?3")
    List<ActivityOrder> findByActivityIdAndUserIdAndActivityOrderStatus(Long activityId, Long userId, ActivityOrder.ActivityOrderStatus activityOrderStatus);

    Optional<ActivityOrder> findFirstByActivityIdAndUserIdAndActivityOrderStatusOrderByCreateTimeAsc(Long activityId, Long userId, ActivityOrder.ActivityOrderStatus activityOrderStatus);

    @Transactional
    @Modifying
    @Query("UPDATE ActivityOrder a " +
            "SET a.usedRaffleCount = a.usedRaffleCount + 1 " +
            "WHERE a.activityOrderId = :activityOrderId")
    void updateUsedRaffleCountByActivityOrderId(Long activityOrderId);

    @Query("""
            select (count(a) > 0) from ActivityOrder a
            where a.userId = ?1 and a.activityId = ?2 and a.activityOrderTypeName = ?3 and a.activityOrderStatus = ?4 and a.createTime between ?5 and ?6""")
    boolean existsByUserIdAndActivityIdAndActivityOrderTypeNameAndActivityOrderStatusAndCreateTimeBetween(Long userId, Long activityId, ActivityOrderType.ActivityOrderTypeName activityOrderTypeName, ActivityOrder.ActivityOrderStatus activityOrderStatus, LocalDateTime createTimeStart, LocalDateTime createTimeEnd);
}
