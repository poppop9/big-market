package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityOrderProductJpa extends JpaRepository<ActivityOrderProduct, Long> {
    @Query("select a from ActivityOrderProduct a where a.activityOrderProductId = ?1")
    ActivityOrderProduct findByActivityOrderProductId(Long activityOrderProductId);
}
