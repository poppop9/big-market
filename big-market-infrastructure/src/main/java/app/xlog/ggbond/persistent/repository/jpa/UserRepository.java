package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
