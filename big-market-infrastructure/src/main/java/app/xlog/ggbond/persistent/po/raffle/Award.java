package app.xlog.ggbond.persistent.po.raffle;

import app.xlog.ggbond.persistent.po.ShardingTable;
import cn.hutool.core.util.IdUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
@Comment("奖品")
public class Award extends ShardingTable {
    @Builder.Default
    private @Comment("奖品ID") Long awardId = IdUtil.getSnowflakeNextId();
    private @Comment("奖品标题") String awardTitle;
    private @Comment("奖品副标题") String awardSubtitle;
}