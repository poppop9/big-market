package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.po.user.User;
import app.xlog.ggbond.persistent.repository.jpa.UserRepository;
import app.xlog.ggbond.user.repository.IStpRepository;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * 用户仓储实现类
 */
@Repository
public class StpRepository implements IStpRepository {

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

}
