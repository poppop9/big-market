package app.xlog.ggbond.security.service;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 安全领域 - 安全服务
 */
@Slf4j
@Service
public class SecurityService implements ISecurityService {

    @Resource
    private ISecurityRepo securityRepo;

    /**
     * 登录
     */
    @Override
    public Boolean doLogin(Long userId, String password) {
        // 进行数据库验证
        if (securityRepo.doLogin(userId, password)) {
            StpUtil.login(userId);
            return true;
        } else {
            log.atInfo().log("用户领域 - 用户登录失败，用户名或密码错误，userId : " + userId + " password : " + password);
            return false;
        }
    }

    /**
     * 查询 - 获取当前登录用户id
     */
    @Override
    public Long getLoginIdDefaultNull() {
        return Optional.ofNullable(StpUtil.getLoginIdDefaultNull())
                .map(Object::toString)
                .map(Long::valueOf)
                .orElse(null);
    }

    /**
     * 查询 - 判断用户是否为黑名单用户
     */
    @Override
    public Boolean isBlacklistUser(Long userId) {
        if (userId == null) {
            // id为空，则为游客，不是黑名单用户
            return true;
        }

        return securityRepo.isBlacklistUser(userId);
    }

    /**
     * 查询 - 查询出所有的黑名单用户
     */
    @Override
    public List<UserBO> queryAllBlacklistUser() {
        return securityRepo.queryAllBlacklistUser();
    }

    /**
     * 查询 - 跟据userId，查询当前用户
     */
    @Override
    public UserBO findUserByUserId(Long userId) {
        return Optional.ofNullable(userId)
                .map(item -> securityRepo.findByUserId(item))
                .orElse(null);
    }

    /**
     * 查询 - 查询用户购买历史
     */
    @Override
    public List<UserPurchaseHistoryBO> findUserPurchaseHistory(Long userId) {
        return securityRepo.findUserPurchaseHistory(userId);
    }

    /**
     * 查询 - 查询最近的购买历史
     */
    @Override
    public List<UserPurchaseHistoryBO> findRecentPurchaseHistory() {
        return securityRepo.findRecentPurchaseHistory();
    }

    /**
     * 插入 - 将黑名单用户放入布隆过滤器
     */
    @Override
    public void insertBlacklistUserListToBloomFilter(List<Long> userIds) {
        securityRepo.insertBlacklistUserListToBloomFilter(userIds);
    }

    /**
     * 插入 - 将当前用户的角色信息放入session
     */
    @Override
    public void insertPermissionIntoSession() {
        StpUtil.getSession().set(
                "role",
                securityRepo.findByUserId(getLoginIdDefaultNull()).getUserRole().name()
        );
    }

    /**
     * 判断 - 检查该用户是否有策略
     */
    @Override
    public boolean existsByUserIdAndActivityId(Long activityId, Long userId) {
        return securityRepo.existsByUserIdAndActivityId(activityId, userId);
    }

    /**
     * 判断 - 判断用户是否有购买历史
     */
    @Override
    public boolean existsUserPurchaseHistory(Long userId) {
        return securityRepo.existsUserPurchaseHistory(userId);
    }

}
