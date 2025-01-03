package app.xlog.ggbond;

/**
 * 全局常量
 */
public class GlobalConstant {

    // 安全领域 - 游客id
    public final static Long tourist = 200L;

    // 安全领域 - token过期时长 : 30天
    public final static Long tokenExpireTime = 30 * 24 * 60 * 60 * 1000L;  // 单位是毫秒

    // 抽奖领域 - redis过期时长 : 3天
    public final static Long redisExpireTime = 3 * 24 * 60 * 60L;  // 单位是秒

    // 抽奖领域 - 生成权重对象的缓存key
    public static String getWeightRandomCacheKey(Long strategyId, String dispatchParam) {
        return strategyId + "_" + dispatchParam + "_WeightRandom";
    }

    // 抽奖领域 - 生成权重对象Map的缓存key
    public static String getWeightRandomMapCacheKey(Long strategyId) {
        return strategyId + "_WeightRandomMap";
    }

    // 抽奖领域 - 生成库存的缓存key
    public static String getAwardCountCacheKey(Long strategyId, Long awardId) {
        return strategyId + "_" + awardId + "_Count";
    }

    // 抽奖领域 - 生成库存Map的缓存key
    public static String getAwardCountMapCacheKey(Long strategyId) {
        return strategyId + "_AwardCountMap";
    }

    // 抽奖领域 - 奖品库存队列
    public static String getAwardCountDecrQueue() {
        return "AwardCountDecrQueue";
    }

    // 抽奖领域 - 黑名单用户列表
    public static String getBlacklistUserList() {
        return "BlacklistUserList";
    }

}
