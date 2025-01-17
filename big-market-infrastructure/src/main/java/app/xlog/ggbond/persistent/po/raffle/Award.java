package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Award", indexes = {
        @Index(columnList = "awardId"),
})
public class Award extends ShardingTableBaseEntity {
    @Builder.Default
    private Long awardId = IdUtil.getSnowflakeNextId();  // 奖品id
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
}
