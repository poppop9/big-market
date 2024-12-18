package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Strategy;
import app.xlog.ggbond.persistent.po.raffle.UserRaffleConfig;
import app.xlog.ggbond.raffle.model.bo.UserRaffleConfigBO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA - 用户抽奖配置仓储
 */
@Repository
public interface UserRaffleConfigRepository extends JpaRepository<UserRaffleConfig, Long> {

    @Query("select u from UserRaffleConfig u where u.userId = ?1 and u.activityId = ?2")
    UserRaffleConfig findByUserIdAndActivityId(Long userId, Long activityId);

    @Query("select u from UserRaffleConfig u where u.userId = ?1 and u.strategyId = ?2")
    UserRaffleConfig findByUserIdAndStrategyId(Long userId, Long strategyId);

    @Transactional
    @Modifying
    @Query("update UserRaffleConfig u set u.raffleTime = ?1 where u.userId = ?2 and u.strategyId = ?3")
    void updateRaffleTimeByUserIdAndStrategyId(Long raffleTime, Long userId, Long strategyId);

}
