package app.xlog.ggbond.security.repository;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;

import java.util.List;

/**
 * 用户仓储接口
 */
public interface ISecurityRepo {

    // 登录
    Boolean doLogin(Long userId, String password);

    // 插入 - 将黑名单用户放入布隆过滤器
    void insertBlacklistUserListToBloomFilter(List<Long> userIds);

    // 修改 - 修改用户密码
    void updatePassword(Long userId, String password);

    // 判断 - 检查该用户是否有策略
    boolean existsByUserIdAndActivityId(Long activityId, Long userId);

    // 查询 - 判断用户是否为黑名单用户
    Boolean isBlacklistUser(Long userId);

    // 查询 - 根据用户id，查询用户
    UserBO findByUserId(Long userId);

    // 查询 - 查询出所有的黑名单用户
    List<UserBO> queryAllBlacklistUser();

    // 查询 - 查询用户购买历史
    List<UserPurchaseHistoryBO> findUserPurchaseHistory(Long userId);

    // 查询 - 查询最近的购买历史
    List<UserPurchaseHistoryBO> findRecentPurchaseHistory();

    // 判断 - 判断用户是否有购买历史
    boolean existsUserPurchaseHistory(Long userId);

    // 查询 - 查询登录用户的信息
    UserBO findLoginUserInfo();
}
