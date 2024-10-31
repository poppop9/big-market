package app.xlog.ggbond.persistent.po;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Award a " +
            "SET a.awardCount = a.awardCount - 1 " +
            "WHERE a.strategyId = :strategyId " +
            "AND a.awardId = :awardId")
    void decrementAwardCountByStrategyIdAndAwardId(Integer strategyId, Integer awardId);

}
