package app.xlog.ggbond.user.service;

import app.xlog.ggbond.user.repository.IStpRepository;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {

    @Resource
    private IStpRepository stpRepository;

    /**
     * 登录
     */
    @Override
    public Boolean doLogin(Long userId, String password) {
        // 进行数据库验证
        if (stpRepository.doLogin(userId, password)) {
            StpUtil.login(userId);
            log.atInfo().log("用户领域 - 用户登录成功，userId : " + userId);
            return true;
        } else {
            log.atInfo().log("用户领域 - 用户登录失败，用户名或密码错误，userId : " + userId);
            return false;
        }
    }

    /**
     * 判断当前登录用户，是否为黑名单用户
     */
    @Override
    public Boolean isBlacklistUser() {
        return Optional.ofNullable(StpUtil.getLoginIdDefaultNull())  // 为null，则为游客
                .map(item -> (Long) item)
                .map(userId -> stpRepository.isBlacklistUser(userId))
                .orElse(false);  // 游客默认不是黑名单用户
    }

    @Override
    public Long queryRaffleTimesByUserId(Long userId) {
        // todo
        return 49L;
    }

}
