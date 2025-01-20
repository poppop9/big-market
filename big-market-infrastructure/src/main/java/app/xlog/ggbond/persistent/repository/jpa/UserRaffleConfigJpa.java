package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.UserRaffleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA - 用户抽奖配置仓储
 */
@Repository
public interface UserRaffleConfigJpa extends JpaRepository<UserRaffleConfig, Long> {

    @Query("select u from UserRaffleConfig u where u.userId = ?1 and u.activityId = ?2")
    UserRaffleConfig findByUserIdAndActivityId(Long userId, Long activityId);

    @Query("select u from UserRaffleConfig u where u.userId = ?1 and u.strategyId = ?2")
    UserRaffleConfig findByUserIdAndStrategyId(Long userId, Long strategyId);

    @Transactional
    @Modifying
    @Query("UPDATE UserRaffleConfig u " +
            "SET u.raffleTime = u.raffleTime + 1 " +
            "WHERE u.userId = :userId " +
            "AND u.strategyId = :strategyId")
    void updateRaffleTimeByUserIdAndStrategyId(Long userId, Long strategyId);

    @Query("select (count(u) > 0) from UserRaffleConfig u where u.userId = ?1 and u.activityId = ?2")
    boolean existsByUserIdAndActivityId(Long userId, Long activityId);
}
