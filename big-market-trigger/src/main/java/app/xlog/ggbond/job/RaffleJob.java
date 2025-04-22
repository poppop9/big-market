package app.xlog.ggbond.job;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleConfigJpa;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 抽奖领域 - 任务
 */
@Slf4j
@Component
public class RaffleJob {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserRaffleConfigJpa userRaffleConfigJpa;

    /**
     * 初始化用户的所有用户活动异或id到布隆过滤器，方便判断是否存在策略
     */
    @PostConstruct
    public void initUserIdActivityIdBloomFilter() {
        RBloomFilter<Long> rBloomFilter = redissonClient.getBloomFilter(GlobalConstant.RedisKey.USER_ACTIVITY_BLOOM_FILTER);
        if (rBloomFilter.isExists()) {
            rBloomFilter.delete();
        }
        rBloomFilter.tryInit(100000L, 0.03);

        userRaffleConfigJpa.findAll().forEach(item -> rBloomFilter.add(item.getUserId() ^ item.getActivityId()));
    }

}