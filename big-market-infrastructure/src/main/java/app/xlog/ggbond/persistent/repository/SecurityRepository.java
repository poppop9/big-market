package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.persistent.po.security.User;
import app.xlog.ggbond.persistent.po.security.UserPurchaseHistory;
import app.xlog.ggbond.persistent.repository.jpa.ActivityJpa;
import app.xlog.ggbond.persistent.repository.jpa.UserJpa;
import app.xlog.ggbond.persistent.repository.jpa.UserPurchaseHistoryJpa;
import app.xlog.ggbond.persistent.repository.jpa.UserRaffleConfigJpa;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RBitSet;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
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
    private UserJpa userJpa;
    @Resource
    private UserRaffleConfigJpa userRaffleConfigJpa;
    @Resource
    private UserPurchaseHistoryJpa userPurchaseHistoryJpa;

    /**
     * 登录
     */
    @Override
    public Boolean doLogin(Long userId, String password) {
        User user = userJpa.findByUserIdAndPassword(userId, password);
        return user != null;
    }

    /**
     * 插入 - 将黑名单用户放入布隆过滤器
     */
    @Override
    public void insertBlacklistUserListToBloomFilter(List<Long> userIds) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(GlobalConstant.RedisKey.BLACKLIST_USER_LIST);
        // 删除旧的布隆过滤器
        if (bloomFilter.isExists()) {
            bloomFilter.delete();
        }
        bloomFilter.tryInit(100000L, 0.03);
        bloomFilter.add(userIds);
    }

    /**
     * 修改 - 修改用户密码
     */
    @Override
    public void updatePassword(Long userId, String password) {
        userJpa.updatePasswordByUserId(password, userId);
    }

    /**
     * 判断 - 检查该用户是否有策略
     */
    @Override
    public boolean existsByUserIdAndActivityId(Long activityId, Long userId) {
        return userRaffleConfigJpa.existsByUserIdAndActivityId(userId, activityId);
    }

    /**
     * 查询 - 判断当前登录用户，是否为黑名单用户
     */
    @Override
    public Boolean isBlacklistUser(Long userId) {
        // 获取布隆过滤器
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(GlobalConstant.RedisKey.BLACKLIST_USER_LIST);
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
                userJpa.findByUserId(userId), UserBO.class
        );
    }

    /**
     * 查询 - 查询出所有的黑名单用户
     */
    @Override
    public List<UserBO> queryAllBlacklistUser() {
        return BeanUtil.copyToList(
                userJpa.findByUserRole(User.UserRole.BLACKLIST), UserBO.class
        );
    }

    /**
     * 查询 - 查询用户购买历史
     */
    @Override
    public List<UserPurchaseHistoryBO> findUserPurchaseHistory(Long userId) {
        return BeanUtil.copyToList(
                userPurchaseHistoryJpa.findByUserIdOrderByCreateTimeDesc(userId),
                UserPurchaseHistoryBO.class
        );
    }

    /**
     * 查询 - 查询最近的购买历史
     */
    @Override
    public List<UserPurchaseHistoryBO> findRecentPurchaseHistory() {
        List<UserPurchaseHistory> userPurchaseHistoryList = userPurchaseHistoryJpa.findByOrderByCreateTimeDesc(PageRequest.of(0, 50));
        return BeanUtil.copyToList(userPurchaseHistoryList, UserPurchaseHistoryBO.class);
    }

    /**
     * 判断 - 判断用户是否有购买历史
     */
    @Override
    public boolean existsUserPurchaseHistory(Long userId) {
        return userPurchaseHistoryJpa.existsByUserId(userId);
    }

    /**
     * 查询 - 查询登录用户的信息
     */
    @Override
    public UserBO findLoginUserInfo() {
        Long userId = Optional.ofNullable(StpUtil.getLoginIdDefaultNull())
                .map(Object::toString)
                .map(Long::valueOf)
                .orElse(null);

        User user = userJpa.findByUserId(userId);
        user.setPassword(null);

        // 获取token信息
        UserBO userBO = BeanUtil.copyProperties(user, UserBO.class);
        userBO.setToken(StpUtil.getTokenInfo().getTokenValue());

        return userBO;
    }

    /**
     * 更新 - 设置即将结束登录的用户状态
     */
    @Override
    public void releaseLoginLock(Long userId) {
        RBitSet bitSet = redissonClient.getBitSet(GlobalConstant.RedisKey.FREQUENT_LOGIN_USER);
        bitSet.clear(userId);
    }

    /**
     * 判断 - 是否能够获取登录锁
     */
    @Override
    public boolean acquireLoginLock(Long userId) {
        RBitSet bitSet = redissonClient.getBitSet(GlobalConstant.RedisKey.FREQUENT_LOGIN_USER);
        boolean wasLocked = bitSet.set(userId, true);
        return !wasLocked;
    }

    /**
     * 读取excel，写入用户购买历史
     */
    @Override
    public void writePurchaseHistoryFromExcel(List<UserPurchaseHistoryBO> userPurchaseHistoryBOList) {
        List<UserPurchaseHistory> userPurchaseHistoryList = BeanUtil.copyToList(userPurchaseHistoryBOList, UserPurchaseHistory.class);
        userPurchaseHistoryJpa.saveAll(userPurchaseHistoryList);
    }

}
