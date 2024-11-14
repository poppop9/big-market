package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;

/**
 * 奖品库存仓储接口
 **/
public interface IAwardInventoryRepository {

    /**
     * 更新奖品库存
     */
    Boolean decreaseAwardCount(Long strategyId, Long awardId);

    /**
     * 将该奖品从缓存中的所有抽奖池里移除
     */
    void removeAwardFromPools(Long strategyId, Long awardId);


    /**
     * 将扣减信息写入队列，缓慢更新数据库的库存数 ---------------------------------------
     */
    // 将扣减信息写入队列
    void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO);

    /**
     * 查询出队列中的一个扣减信息
     */
    DecrQueueVO queryDecrAwardCountFromQueue();

    /**
     * 根据策略id，奖品id，更新数据库中对应奖品的库存
     */
    void updateAwardCount(DecrQueueVO decrQueueVO);
}
