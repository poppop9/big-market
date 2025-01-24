package app.xlog.ggbond.persistent.repository.jpa;

import app.xlog.ggbond.persistent.po.activity.ActivityRedeemCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JPA - 活动兑换码
 */
@Repository
public interface ActivityRedeemCodeJpa extends JpaRepository<ActivityRedeemCode, Long> {

    boolean existsActivityRedeemCodeByRedeemCode(String redeemCode);

    @Query("select (count(a) > 0) from ActivityRedeemCode a where a.redeemCode = ?1")
    boolean existsByRedeemCode(String redeemCode);

    @Query("""
            select (count(a) > 0) from ActivityRedeemCode a
            where a.activityId = ?1 and a.isUsed = ?2 and a.redeemCode = ?3""")
    boolean existsByActivityIdAndIsUsedAndRedeemCode(Long activityId, Boolean isUsed, String redeemCode);

    @Query("select a from ActivityRedeemCode a where a.redeemCode = ?1")
    ActivityRedeemCode findByRedeemCode(String redeemCode);

    @Transactional
    @Modifying
    @Query("update ActivityRedeemCode a set a.userId = ?1, a.isUsed = ?2 where a.redeemCode = ?3")
    int updateUserIdAndIsUsedByRedeemCode(Long userId, Boolean isUsed, String redeemCode);
}
