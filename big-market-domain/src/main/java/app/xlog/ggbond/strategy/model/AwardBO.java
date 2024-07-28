package app.xlog.ggbond.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/*
对应infrastructure层的Award对象，用于业务逻辑处理，所以简化了一些属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardBO {
    private Integer strategyId;
    private Integer awardId;
    private Integer awardCount;
    private float awardRate;
    private String rules;
}
