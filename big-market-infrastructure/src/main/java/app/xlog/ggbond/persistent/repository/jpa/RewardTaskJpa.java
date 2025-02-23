package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.reward.RewardTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA - 返利任务
 */
@Repository
public interface RewardTaskJpa extends JpaRepository<RewardTask, Long> {

    @Transactional
    @Modifying
    @Query("update RewardTask a set a.isIssued = ?1 where a.rewardId = ?2")
    void updateIsIssuedByAwardIssuanceId(Boolean isIssued, Long rewardId);

    @Query("select a from RewardTask a where a.isIssued = ?1 and a.createTime between ?2 and ?3")
    List<RewardTask> findByIsIssuedAndCreateTimeBetween(Boolean isIssued, LocalDateTime createTimeStart, LocalDateTime createTimeEnd);

}
