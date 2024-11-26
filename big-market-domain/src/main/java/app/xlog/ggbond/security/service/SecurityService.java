package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.repository.ISecurityRepository;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 安全领域 - 安全服务
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

    // --------------------------------
    // ------------- 查询 --------------
    // --------------------------------

    /**
     * 获取当前登录用户id
     */
    @Override
    public Long getLoginIdDefaultNull() {
        return (Long) StpUtil.getLoginIdDefaultNull();
    }

    /**
     * 判断用户是否为黑名单用户
     */
    @Override
    public Boolean isBlacklistUser(Long userId) {
        if (userId == null) {
            // id为空，则为游客，不是黑名单用户
            return true;
        }

        return securityRepository.isBlacklistUser(userId);
    }

    /**
     * 查询用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId) {
        UserBO userBO = securityRepository.findByUserId(userId);
        return userBO.getRaffleTimes();
    }


}
