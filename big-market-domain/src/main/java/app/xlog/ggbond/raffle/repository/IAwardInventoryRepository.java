package app.xlog.ggbond.raffle.repository;

public interface IAwardInventoryRepository {

    /*
    更新奖品库存  --------------------------------------------------------------
     */
    Boolean decreaseAwardCount(Integer strategyId, Integer awardId);

    void removeAwardFromPools(Integer strategyId, Integer awardId);

}
