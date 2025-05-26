package app.xlog.ggbond.persistent.po.reward;

import app.xlog.ggbond.persistent.po.SingleTable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 兑换奖品流水
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ExchangePrizesLog", indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "activityId"),
        @Index(columnList = "exchangePrizesId"),
        @Index(columnList = "userId, activityId"),
})
@Comment("兑换奖品流水")
public class ExchangePrizesLog extends SingleTable {
    private Long activityId;  // 活动id
    private Long userId;  // 用户id
    private Long exchangePrizesId;  // 兑换奖品id
}
