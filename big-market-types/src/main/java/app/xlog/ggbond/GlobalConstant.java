package app.xlog.ggbond;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 全局常量
 */
public class GlobalConstant {

    public static LocalDateTime validLocalDateTimeMax = LocalDateTime.of(9999, 12, 31, 23, 59, 59, 0);

    /**
     * redis常量
     */
    public static class RedisKey {
        public final static Long REDIS_EXPIRE_TIME = 3 * 24 * 60 * 60L;  // 抽奖领域 - redis过期时长 : 3天（单位是秒）

        // 抽奖领域 - 奖品库存扣减队列
        public static String AWARD_COUNT_DECR_QUEUE = "AWARD_COUNT_DECR_QUEUE";

        // 抽奖领域 - 黑名单用户列表
        public static String BLACKLIST_USER_LIST = "BLACKLIST_USER_LIST";

        // 抽奖领域 - 用户是否在抽奖中的bitmap
        public static String USER_IN_RAFFLE_BIT_SET = "USER_IN_RAFFLE_BIT_SET";

        // 活动领域 - 用户是否在抽奖中的bitmap
        public static String USER_IN_CONSUME_AO = "USER_IN_CONSUME_AO";

        // 抽奖领域 - 检查过期待支付活动单队列
        public static String CHECK_EXPIRE_PENDING_PAYMENT_AO_QUEUE = "CHECK_EXPIRE_PENDING_PAYMENT_AO_QUEUE";
        public static Long PENDING_PAYMENT_AO_EXPIRE_TIME = 10 * 60L;  // 待支付活动单过期时间 : 10分钟（单位是秒）

        // 抽奖领域 - 生成权重对象的缓存key
        public static String getWeightRandomCacheKey(Long strategyId, String dispatchParam) {
            return strategyId + "_" + dispatchParam + "_WEIGHT_RANDOM";
        }

        // 抽奖领域 - 生成权重对象Map的缓存key
        public static String getWeightRandomMapCacheKey(Long strategyId) {
            return strategyId + "_WEIGHT_RANDOM_MAP";
        }

        // 抽奖领域 - 生成库存的缓存key
        public static String getAwardCountCacheKey(Long strategyId, Long awardId) {
            return strategyId + "_" + awardId + "_COUNT";
        }

        // 抽奖领域 - 生成库存Map的缓存key
        public static String getAwardCountMapCacheKey(Long strategyId) {
            return strategyId + "_AWARD_COUNT_MAP";
        }

        // 抽奖领域 - 生成奖品列表的缓存key
        public static String getAwardListCacheKey(Long strategyId) {
            return strategyId + "_AWARD_LIST";
        }

        // 安全领域 - 用户是否频繁登录的bitmap
        public static String FREQUENT_LOGIN_USER = "FREQUENT_LOGIN_USER";
    }

    /**
     * Kafka常量
     */
    public static class KafkaConstant {
        public final static String GROUP_ID = "consumer_group_1";

        // 抽奖领域 - 扣减奖品库存
        public final static String DECR_AWARD_INVENTORY = "Raffle-DecrAwardInventory";

        // 返利领域 - 发放奖品任务
        public static final String REWARD_TASK = "Reward-RewardTask";
        public static final String REWARD_EFFECTIVE_ACTIVITY_ORDER_TASK = "Activity-RewardEffectiveActivityOrderTask";
    }

    // 抽奖，推荐领域 - 奖品级别
    @Getter
    @AllArgsConstructor
    public enum AwardLevel {
        LOW("0-50", null, 4d, 50L, 2),
        MIDDLE("50-200", "抽奖 10 次后解锁", 3d, 10L, 6),
        HIGH("200-2000", "抽奖 20 次后解锁", 1d, 1L, 9),
        ;

        private final String priceRange;
        private final String awardSubtitle;
        private final Double awardRate;
        private final Long awardCount;
        private final int initAwardSort;

        public static AwardLevel getNameByPriceRange(String priceRange) {
            for (AwardLevel awardLevel : AwardLevel.values()) {
                if (awardLevel.getPriceRange().equals(priceRange)) {
                    return awardLevel;
                }
            }
            return null;
        }
    }

    // 活动领域 - 待支付活动单的过期时间
    public final static Long ACTIVITY_ORDER_EXPIRE_TIME = 10 * 60L;  // 10分钟（单位是秒）

    // 返利领域 - 扫描多久之前的中奖记录
    public final static Long SCAN_REWARD_TASK_TIME = 10 * 60L;  // 10分钟（单位是秒）

    // 活动领域 - 扫描多久之前的发送有效活动单记录
    public final static Long SCAN_ISSUANCE_EFFECTIVE_ACTIVITY_ORDER_TIME = 10 * 60L;  // 10分钟（单位是秒）

}
