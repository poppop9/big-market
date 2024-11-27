package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * JPA - 用户仓储
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.userId = ?1 and u.password = ?2")
    User findByUserIdAndPassword(Long userId, String password);

    @Query("select u from User u where u.userId = ?1")
    User findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update User u set u.strategyRaffleTimeMap = ?1 where u.userId = ?2")
    void updateStrategyRaffleTimeMapByUserId(Map<Long, Long> strategyRaffleTimeMap, Long userId);
}
