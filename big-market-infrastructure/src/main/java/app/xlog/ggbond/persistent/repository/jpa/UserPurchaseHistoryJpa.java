package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.security.UserPurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * JPA - 用户购买历史仓储
 */
public interface UserPurchaseHistoryJpa extends JpaRepository<UserPurchaseHistory, Long> {
    @Query("select u from UserPurchaseHistory u where u.userId = ?1 order by u.createTime DESC")
    List<UserPurchaseHistory> findByUserIdOrderByCreateTimeDesc(Long userId);
}
