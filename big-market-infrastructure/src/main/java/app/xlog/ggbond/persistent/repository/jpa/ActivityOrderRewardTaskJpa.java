package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityOrderRewardTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA - 活动单发放任务
 */
@Repository
public interface ActivityOrderRewardTaskJpa extends JpaRepository<ActivityOrderRewardTask, Long> {

    @Transactional
    @Modifying
    @Query("update ActivityOrderRewardTask a set a.isIssued = ?1 where a.activityOrderRewardTaskId = ?2")
    void updateIsIssuedByActivityOrderIssuanceTaskId(Boolean isIssued, Long activityOrderRewardTaskId);

    @Query("select a from ActivityOrderRewardTask a where a.isIssued = ?1 and a.createTime between ?2 and ?3")
    List<ActivityOrderRewardTask> findByIsIssuedAndCreateTimeBetween(Boolean isIssued, LocalDateTime createTimeStart, LocalDateTime createTimeEnd);

}
