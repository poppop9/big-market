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

}
