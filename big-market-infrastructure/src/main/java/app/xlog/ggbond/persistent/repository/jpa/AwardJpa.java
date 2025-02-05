package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Award;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * JPA - 奖品仓储
 */
@Repository
public interface AwardJpa extends JpaRepository<Award, Long> {

    @Query("select a from Award a where a.awardId = ?1")
    Award findByAwardId(Long awardId);

    @Query("select a from Award a where a.awardId in ?1")
    List<Award> findByAwardIdIn(Collection<Long> awardIds);
}
