package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.raffle.Award;
import app.xlog.ggbond.persistent.po.raffle.RafflePool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RafflePoolRepository extends JpaRepository<RafflePool, Long> {
}
