package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Strategy;
import app.xlog.ggbond.persistent.po.raffle.UserRaffleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JPA - 用户抽奖配置仓储
 */
@Repository
public interface UserRaffleConfigRepository extends JpaRepository<UserRaffleConfig, Long> {
    @Query("select u from UserRaffleConfig u where u.userId = ?1 and u.activityId = ?2")
    UserRaffleConfig findByUserIdAndActivityId(Long userId, Long activityId);
}
