package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTableBaseEntity;
import app.xlog.ggbond.persistent.util.JpaDefaultValue;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 奖品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Award", indexes = {
        @Index(columnList = "strategyId"),
        @Index(columnList = "awardId"),
        @Index(columnList = "strategyId, awardId")
})
public class Award extends ShardingTableBaseEntity {
    @Builder.Default
    private Long awardId = IdUtil.getSnowflakeNextId();  // 奖品id todo 最好保证生成的一组奖品的id的hash取余都是一样的
    private String awardTitle;  // 奖品标题
    private String awardSubtitle;  // 奖品副标题
}
