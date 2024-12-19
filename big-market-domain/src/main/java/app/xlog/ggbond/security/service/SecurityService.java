package app.xlog.ggbond.security.service;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserRaffleHistoryBO;
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
            StpUtil.login(userId, GlobalConstant.tokenExpireTime);
            // todo 一旦登录，就会在tokenExpireTime后，清除redis中的权重对象，以及库存，加入redis延迟队列
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
                .orElse(GlobalConstant.tourist);
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
     * 查询 - 查询用户的抽奖次数
     */
    @Override
    public Long queryRaffleTimesByUserId(Long userId, Long strategyId) {
        return securityRepo.queryRaffleTimesByUserId(userId, strategyId);
    }

    /**
     * 查询 - 查询用户某个活动的中奖奖品信息
     */
    @Override
    public List<UserRaffleHistoryBO> findWinningAwardsInfo(Long activityId, Long userId) {
        // 跟据活动id，用户id，查询用户的策略id
        Long strategyId = securityRepo.findStrategyIdByActivityIdAndUserId(activityId, userId);
        return securityRepo.getWinningAwardsInfo(userId, strategyId);
    }

    /**
     * 查询 - 根据活动id，用户id，查询用户的策略id
     */
    @Override
    public Long findStrategyIdByActivityIdAndUserId(Long activityId, Long userId) {
        return securityRepo.findStrategyIdByActivityIdAndUserId(activityId, userId);
    }

    /**
     * 插入 - 将黑名单用户放入布隆过滤器
     */
    @Override
    public void insertBlacklistUserListToBloomFilter(List<Long> userIds) {
        securityRepo.insertBlacklistUserListToBloomFilter(userIds);
    }

}
