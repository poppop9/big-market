package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
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
     * 查询 - 查询用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId, Long strategyId) {
        UserBO userBO = securityRepo.findByUserId(userId);
        return userBO.getStrategyRaffleTimeMap().getOrDefault(strategyId, 0L);
    }

    /**
     * 查询 - 跟据userId，查询当前用户
     */
    @Override
    public UserBO findUserByUserId(Long userId) {
        UserBO user = securityRepo.findByUserId(userId);
        return user;
    }

    /**
     * 插入 - 将黑名单用户放入布隆过滤器
     */
    @Override
    public void insertBlacklistUserListToBloomFilter(List<Long> userIds) {
        securityRepo.insertBlacklistUserListToBloomFilter(userIds);
    }

}
