package app.xlog.ggbond.persistent.po;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 策略表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Strategy")
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long strategyId;  // 策略id
    private String strategyDesc;  // 策略描述
    private String rules;  // 策略的规则，json格式
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
