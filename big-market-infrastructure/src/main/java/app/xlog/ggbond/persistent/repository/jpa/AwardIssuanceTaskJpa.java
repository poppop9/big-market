package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.awardIssuance.AwardIssuanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA - 奖品发放任务
 */
@Repository
public interface AwardIssuanceTaskJpa extends JpaRepository<AwardIssuanceTask, Long> {

    @Transactional
    @Modifying
    @Query("update AwardIssuanceTask a set a.isIssued = ?1 where a.awardIssuanceId = ?2")
    void updateIsIssuedByAwardIssuanceId(Boolean isIssued, Long awardIssuanceId);

}
