package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.awardIssuance.AwardIssuanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA - 奖品发放任务
 */
@Repository
public interface AwardIssuanceTaskJpa extends JpaRepository<AwardIssuanceTask, Long> {

    @Transactional
    @Modifying
    @Query("update AwardIssuanceTask a set a.isIssued = ?1 where a.awardIssuanceId = ?2")
    void updateIsIssuedByAwardIssuanceId(Boolean isIssued, Long awardIssuanceId);

    @Query("select a from AwardIssuanceTask a where a.isIssued = ?1 and a.createTime between ?2 and ?3")
    List<AwardIssuanceTask> findByIsIssuedAndCreateTimeBetween(Boolean isIssued, LocalDateTime createTimeStart, LocalDateTime createTimeEnd);
}
