package app.xlog.ggbond.user.service;

import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Override
    public Boolean isBlacklistUser(Integer userId) {
        // todo
        return userId == 404;
    }

    @Override
    public Integer queryRaffleTimesByUserId(Integer userId) {
        // todo
        return 49;
    }
}
