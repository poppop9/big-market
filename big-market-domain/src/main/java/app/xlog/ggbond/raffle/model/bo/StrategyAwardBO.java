package app.xlog.ggbond.raffle.model.bo;

/**
 * 策略·奖品中间表
 */
public class StrategyAwardBO {
    private Long strategyId;  // 策略id（同1个strategyId下，一定会有9个awardId）
    private Long awardId;  // 奖品id
    private Long awardCount;  // 奖品库存
    private Integer awardSort;  // 奖品在前端的排序
}