package app.xlog.ggbond.raffle.repository;

import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;

public interface IAwardInventoryRepository {

    /**
     * 更新奖品库存 --------------------------------------------------------------
     **/
    Boolean decreaseAwardCount(Integer strategyId, Integer awardId);

    void removeAwardFromPools(Integer strategyId, Integer awardId);


    /**
     * 将扣减信息写入队列，缓慢更新数据库的库存数 ---------------------------------------
     */
    // 将扣减信息写入队列
    void addDecrAwardCountToQueue(DecrQueueVO decrQueueVO);

    // 查询出队列中的一个扣减信息
    DecrQueueVO queryDecrAwardCountFromQueue();

    // 根据策略id，奖品id，更新数据库中对应奖品的库存
    void updateAwardCount(DecrQueueVO decrQueueVO);
}
