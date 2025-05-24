package app.xlog.ggbond.persistent.po.reward;

import app.xlog.ggbond.persistent.po.ShardingTable;
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
 * 兑换奖品
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ExchangePrizes", indexes = {
        @Index(columnList = "activityId"),
        @Index(columnList = "exchangePrizesId"),
})
@Comment("兑换奖品")
public class ExchangePrizes extends SingleTable {
    private Long activityId;  // 活动id
    private Long exchangePrizesId;  // 兑换奖品id
    private String exchangePrizesName;  // 兑换奖品名称
    private Long points;  // 兑换该奖品所需的积分
}