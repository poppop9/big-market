package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.security.UserPurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA - 用户购买历史仓储
 */
public interface UserPurchaseHistoryJpa extends JpaRepository<UserPurchaseHistory, Long> {
}
