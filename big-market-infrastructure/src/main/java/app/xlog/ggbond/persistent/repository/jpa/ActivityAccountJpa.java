package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA - 活动账户仓储
 */
public interface ActivityAccountJpa extends JpaRepository<ActivityAccount, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE ActivityAccount a " +
            "SET a.availableRaffleCount = a.availableRaffleCount + :availableRaffleCount " +
            "WHERE a.userId = :userId " +
            "AND a.activityId = :activityId")
    void updateAvailableRaffleCountByUserIdAndActivityId(Long availableRaffleCount, Long userId, Long activityId);

    @Query("select (count(a) > 0) from ActivityAccount a where a.userId = ?1 and a.activityId = ?2")
    boolean existsByUserIdAndActivityId(Long userId, Long activityId);

    @Transactional
    @Modifying
    @Query("UPDATE ActivityAccount a " +
            "SET a.availableRaffleCount = a.availableRaffleCount - 1 " +
            "WHERE a.activityId = :activityId " +
            "AND a.userId = :userId")
    void updateAvailableRaffleCountByActivityIdAndUserId(Long activityId, Long userId);

    @Query("select a from ActivityAccount a where a.userId = ?1 and a.activityId = ?2")
    ActivityAccount findByUserIdAndActivityId(Long userId, Long activityId);

    @Transactional
    @Modifying
    @Query("update ActivityAccount a set a.balance = ?1 where a.userId = ?2 and a.activityId = ?3")
    int updateBalanceByUserIdAndActivityId(Double balance, Long userId, Long activityId);
}
