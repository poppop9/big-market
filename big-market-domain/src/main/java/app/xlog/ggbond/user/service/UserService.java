package app.xlog.ggbond.user.service;

import app.xlog.ggbond.user.IUserRepository;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Resource
    private IUserRepository userRepository;

    @Override
    public Boolean isBlacklistUser() {
        return Optional.ofNullable(StpUtil.getLoginIdDefaultNull().toString())
                .map(userId -> userRepository)  // todo 去数据库中查询是否为黑名单用户
                .orElse(false);  // 游客默认不是黑名单用户
    }

    @Override
    public Long queryRaffleTimesByUserId(Long userId) {
        // todo
        return 49L;
    }
}
