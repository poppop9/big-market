package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.reward.PointsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA - 积分流水
 */
@Repository
public interface PointsLogJpa extends JpaRepository<PointsLog, Long> {

    @Transactional
    @Modifying
    @Query("update PointsLog p set p.isIssued = ?1 where p.pointsLogId = ?2")
    void updateIsIssuedByPointsLogId(Boolean isIssued, Long pointsLogId);
}
