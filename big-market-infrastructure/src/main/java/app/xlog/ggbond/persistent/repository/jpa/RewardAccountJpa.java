package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.reward.RewardAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA - 返利账户
 */
@Repository
public interface RewardAccountJpa extends JpaRepository<RewardAccount, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE RewardAccount r " +
            "SET r.points = r.points + :points " +
            "WHERE r.activityId = :activityId " +
            "AND r.userId = :userId")
    void rechargeRewardAccountPoints(Long points, Long activityId, Long userId);

    @Query("select (count(r) > 0) from RewardAccount r where r.activityId = ?1 and r.userId = ?2")
    boolean existsByActivityIdAndUserId(Long activityId, Long userId);

    @Query("select r from RewardAccount r where r.userId = ?1 and r.activityId = ?2")
    RewardAccount findByUserIdAndActivityId(Long userId, Long activityId);

    @Transactional
    @Modifying
    @Query("update RewardAccount r set r.points = ?1 where r.activityId = ?2 and r.userId = ?3")
    int updatePointsByActivityIdAndUserId(Long points, Long activityId, Long userId);
}
