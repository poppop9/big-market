package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * JPA - 用户仓储
 */
@Repository
public interface UserJpa extends JpaRepository<User, Long> {

    @Query("select u from User u where u.userId = ?1 and u.password = ?2")
    User findByUserIdAndPassword(Long userId, String password);

    @Query("select u from User u where u.userId = ?1")
    User findByUserId(Long userId);

    @Query("select u from User u where u.userRole = ?1")
    List<User> findByUserRole(User.UserRole userRole);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?1 where u.userId = ?2")
    void updatePasswordByUserId(String password, Long userId);

    @Query("select (count(u) > 0) from User u where u.userId = ?1 and u.password = ?2")
    boolean existsByUserIdAndPassword(Long userId, String password);
}
