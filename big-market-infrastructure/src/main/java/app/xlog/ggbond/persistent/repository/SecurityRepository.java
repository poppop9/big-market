package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.persistent.po.security.User;
import app.xlog.ggbond.persistent.po.security.UserRaffleConfig;
import app.xlog.ggbond.persistent.po.security.UserRaffleHistory;
import app.xlog.ggbond.persistent.repository.jpa.ActivityJpa;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleConfigRepository;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleHistoryRepository;
import app.xlog.ggbond.persistent.repository.jpa.UserRepository;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserRaffleConfigBO;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 安全领域 - 安全仓储实现类
 */
@Repository
public class SecurityRepository implements ISecurityRepo {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ActivityJpa activityJpa;
    @Resource
    private UserRepository userRepository;
    @Resource
    private UserRaffleConfigRepository userRaffleConfigRepository;
    @Resource
    private UserRaffleHistoryRepository userRaffleHistoryRepository;

    /**
     * 登录
     */
    @Override
    public Boolean doLogin(Long userId, String password) {
        User user = userRepository.findByUserIdAndPassword(userId, password);
        return user != null;
    }

    /**
     * 查询 - 判断当前登录用户，是否为黑名单用户
     */
    @Override
    public Boolean isBlacklistUser(Long userId) {
        // 获取布隆过滤器
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(GlobalConstant.getBlacklistUserList());
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(100000L, 0.03);
        }

        boolean isBlackListUser = bloomFilter.contains(userId);
        if (isBlackListUser) {
            // 如果布隆过滤器中存在，则不一定是黑名单用户，所以要多次判断
            isBlackListUser = Stream.iterate(0, i -> i < 3, i -> i + 1)
                    .allMatch(item -> bloomFilter.contains(userId));
        } else {
            // 如果布隆过滤器中不存在，则一定不是黑名单用户
            return false;
        }

        return isBlackListUser;
    }

    /**
     * 查询 - 根据用户id，查询用户
     */
    @Override
    public UserBO findByUserId(Long userId) {
        return BeanUtil.copyProperties(
                userRepository.findByUserId(userId), UserBO.class
        );
    }

    /**
     * 查询 - 查询出所有的黑名单用户
     */
    @Override
    public List<UserBO> queryAllBlacklistUser() {
        return BeanUtil.copyToList(
                userRepository.findByUserRole(User.UserRole.BLACKLIST), UserBO.class
        );
    }

    /**
     * 查询 - 跟据活动id，用户id，查询用户的策略id
     */
    @Override
    public Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId) {
        UserRaffleConfig userConfig = userRaffleConfigRepository.findByUserIdAndActivityId(userId, activityId);
        return Optional.ofNullable(userConfig.getStrategyId())
                .orElseGet(() -> activityJpa.findByActivityId(activityId).getDefaultStrategyId());
    }

    /**
     * 查询 - 根据用户id，策略id，查询用户的抽奖历史
     */
    @Override
    public List<UserRaffleHistoryBO> getWinningAwardsInfo(Long userId, Long strategyId) {
        List<UserRaffleHistory> list = userRaffleHistoryRepository.findByUserIdAndStrategyIdOrderByCreateTimeAsc(userId, strategyId);
        return BeanUtil.copyToList(list, UserRaffleHistoryBO.class);
    }

    /**
     * 查询 - 查询当前用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId, Long strategyId) {
        UserRaffleConfig userRaffleConfig = userRaffleConfigRepository.findByUserIdAndStrategyId(userId, strategyId);
        return BeanUtil.copyProperties(userRaffleConfig, UserRaffleConfigBO.class).getRaffleTime();
    }

    /**
     * 插入 - 将黑名单用户放入布隆过滤器
     */
    @Override
    public void insertBlacklistUserListToBloomFilter(List<Long> userIds) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(GlobalConstant.getBlacklistUserList());
        // 删除旧的布隆过滤器
        if (bloomFilter.isExists()) {
            bloomFilter.delete();
        }

        bloomFilter.tryInit(100000L, 0.03);
        bloomFilter.add(userIds);
    }

}
