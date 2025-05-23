package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 安全领域 - 安全服务接口
 */
public interface ISecurityService {

    // 登录
    Boolean doLogin(Long userId, String password);

    // 跟据 token 退出登录
    void logoutByToken(String token);

    // 查询 - 获取当前登录用户id
    Long getLoginIdDefaultNull();

    // 查询 - 判断用户是否是黑名单用户
    Boolean isBlacklistUser(Long userId);

    // 查询 - 查询出所有的黑名单用户
    List<UserBO> queryAllBlacklistUser();

    // 查询 - 跟据userId，查询当前用户
    UserBO findUserByUserId(Long userId);

    // 查询 - 查询用户购买历史
    List<UserPurchaseHistoryBO> findUserPurchaseHistory(Long userId);

    // 查询 - 查询最近的购买历史
    List<UserPurchaseHistoryBO> findRecentPurchaseHistory();

    // 插入 - 将黑名单用户放入布隆过滤器
    void insertBlacklistUserListToBloomFilter(List<Long> userIds);

    // 插入 - 将当前用户的角色信息放入session
    void insertPermissionIntoSession();

    // 判断 - 检查该用户是否有策略
    boolean existsByUserIdAndActivityId(Long activityId, Long userId);

    // 判断 - 判断用户是否有购买历史
    boolean existsUserPurchaseHistory(Long userId);

    // 查询 - 查询登录用户的信息
    UserBO findLoginUserInfo();

    // 更新 - 设置即将结束登录的用户状态
    void releaseLoginLock(Long userId);

    // 判断 - 判断是否可以获取登录锁
    boolean acquireLoginLock(Long userId);

    // 读取excel，写入用户购买历史
    void writePurchaseHistoryFromExcel(MultipartFile file);

    // 查询用户的购买历史记录
    List<UserPurchaseHistoryBO> findUserPurchaseHistory();

}
