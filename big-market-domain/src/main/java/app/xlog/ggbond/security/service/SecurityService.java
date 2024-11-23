package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.repository.ISecurityRepository;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 安全服务
 */
@Slf4j
@Service
public class SecurityService implements ISecurityService {

    @Resource
    private ISecurityRepository securityRepository;

    /**
     * 登录
     */
    @Override
    public Boolean doLogin(Long userId, String password) {
        // 进行数据库验证
        if (securityRepository.doLogin(userId, password)) {
            StpUtil.login(userId);
            return true;
        } else {
            log.atInfo().log("用户领域 - 用户登录失败，用户名或密码错误，userId : " + userId + " password : " + password);
            return false;
        }
    }

    /**
     * 查询用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId) {
        UserBO userBO = securityRepository.findByUserId(userId);
        return userBO.getRaffleTimes();
    }

    /**
     * 判断当前登录用户，是否为黑名单用户
     */
    @Override
    public Boolean isBlacklistUser() {
        Boolean isBlacklistUser = Optional.ofNullable(StpUtil.getLoginIdDefaultNull())
                .map(item -> (Long) item)
                .map(userId -> securityRepository.isBlacklistUser(userId))
                .orElse(false);  // 为null，则为游客，游客默认不是黑名单用户
        return isBlacklistUser;
    }

}
