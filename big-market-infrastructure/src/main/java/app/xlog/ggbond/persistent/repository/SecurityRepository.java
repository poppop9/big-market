package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.po.security.User;
import app.xlog.ggbond.persistent.repository.jpa.UserRepository;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.repository.ISecurityRepository;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * 安全领域 - 安全仓储实现类
 */
@Repository
public class SecurityRepository implements ISecurityRepository {

    @Resource
    private UserRepository userRepository;

    /**
     * 登录
     */
    @Override
    public Boolean doLogin(Long userId, String password) {
        return userRepository.findByUserIdAndPassword(userId, password) != null;
    }

    /**
     * 判断当前登录用户，是否为黑名单用户
     */
    @Override
    public Boolean isBlacklistUser(Long userId) {
        return userRepository.findByUserId(userId).getUserRole() == User.UserRole.BLACKLIST;
    }

    /**
     * 根据用户id，查询用户
     */
    @Override
    public UserBO findByUserId(Long userId) {
        return BeanUtil.copyProperties(
                userRepository.findByUserId(userId), UserBO.class
        );
    }

}
