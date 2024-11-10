package app.xlog.ggbond.user.service;

import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Override
    public Boolean isBlacklistUser(Long userId) {
        // todo 用户为404，则是黑名单用户
        return userId == 404;
    }

    @Override
    public Long queryRaffleTimesByUserId(Long userId) {
        // todo
        return 49L;
    }
}
