package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import app.xlog.ggbond.persistent.util.JpaDefaultValue;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 策略
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Strategy", indexes = {
        @Index(columnList = "activityId")
})
public class Strategy extends ShardingTableBaseEntity {
    private Long activityId;  // 活动id
    @Column(unique = true)
    @JpaDefaultValue(howToCreate = "cn.hutool.core.util.IdUtil.getSnowflakeNextId()")
    private Long strategyId;  // 策略id，全局唯一（不同活动的策略id也不能重复）
    private String strategyDesc;  // 策略描述
}
