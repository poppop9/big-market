package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Award;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Award a " +
            "SET a.awardCount = a.awardCount - 1 " +
            "WHERE a.strategyId = :strategyId " +
            "AND a.awardId = :awardId")
    void decrementAwardCountByStrategyIdAndAwardId(Long strategyId, Long awardId);

    @Query("select a from Award a where a.strategyId = ?1")
    List<Award> findByStrategyId(Long strategyId);

    @Query("select a from Award a where a.awardCount between ?1 and ?2")
    List<Award> findByAwardCountBetween(Long awardCountStart, Long awardCountEnd);

    @Query("select a from Award a where a.awardTitle like concat('%', ?1, '%') and a.awardCount between ?2 and ?3")
    List<Award> findByAwardTitleContainsAndAwardCountBetween(String awardTitle, Long awardCountStart, Long awardCountEnd);

    @Query("select a from Award a where a.awardId = ?1")
    Award findByAwardId(Long awardId);

}
