package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import cn.hutool.core.lang.WeightRandom;

/**
 * 抽奖领域 - 抽奖调度仓库
 */
public interface IRaffleDispatchRepo {

    // 权重对象 - 从 redis 中查询出指定的权重对象
    WeightRandom<Long> findWeightRandom(Long strategyId, String dispatchParam);

    // 抽奖池 - 将该奖品从缓存中的所有抽奖池里移除
    void removeAwardFromPools(Long strategyId, Long awardId);

    // 库存 - 根据策略id，奖品id，更新数据库中对应奖品的库存
    void updateAwardCount(DecrQueueVO decrQueueVO);

    // 库存 - 更新奖品库存
    Boolean decreaseAwardCount(Long strategyId, Long awardId);

    // 库存 - 将扣减信息写入队列
    void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO);

    // 库存 - 查询出队列中的一个扣减信息
    DecrQueueVO queryDecrAwardCountFromQueue();


    // 抽奖次数 - 给用户的指定策略增加抽奖次数
    void addUserRaffleTimeByStrategyId(Long userId, Long strategyId);

    // 用户抽奖历史 - 添加用户抽奖流水记录
    void addUserRaffleFlowRecordFilter(Long userId, Long strategyId, Long awardId);

}
