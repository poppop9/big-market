package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import cn.hutool.core.lang.WeightRandom;

/**
 * 抽奖领域 - 抽奖调度仓库
 */
public interface IRaffleDispatchRepo {

    // 抽奖池 - 将该奖品从缓存中的所有抽奖池里移除
    void removeAwardFromPools(Long strategyId, Long awardId);

    // 库存 - 根据策略id，奖品id，更新数据库中对应奖品的库存
    void updateAwardCount(DecrQueueVO decrQueueVO);

    // 库存 - 更新奖品库存
    Boolean decreaseAwardCount(Long strategyId, Long awardId);

    // 库存 - 将扣减信息写入队列
    void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO);

    // 库存 - 将扣减信息写入kafka
    void addDecrAwardCountToMQ(DecrQueueVO decrQueueVO);

    // 库存 - 查询出队列中的一个扣减信息
    DecrQueueVO queryDecrAwardCountFromQueue();

    // 库存 - 更新所有奖品库存的过期时间
    void updateAllAwardCountExpireTime(Long strategyId);

    // 抽奖次数 - 给用户的指定策略增加抽奖次数
    void addUserRaffleTimeByStrategyId(Long userId, Long strategyId);

    // 用户抽奖历史 - 添加用户抽奖流水记录
    Long addUserRaffleFlowRecordFilter(Long userId, Long strategyId, Long awardId);

    // 权重对象 - 更新所有权重对象Map的过期时间
    void updateAllWeightRandomExpireTime2(Long strategyId);

    // 奖品 - 更新所有奖品列表的过期时间
    void updateAllAwardListExpireTime(Long strategyId);

}
