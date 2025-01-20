package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.UserRaffleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA - 用户抽奖历史仓储
 */
@Repository
public interface UserRaffleHistoryJpa extends JpaRepository<UserRaffleHistory, Long> {

    @Query("select u from UserRaffleHistory u where u.userId = ?1 and u.strategyId = ?2 order by u.createTime")
    List<UserRaffleHistory> findByUserIdAndStrategyIdOrderByCreateTimeAsc(Long userId, Long strategyId);

}
