package app.xlog.ggbond.user.service;

import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Override
    public Boolean isBlacklistUser(Integer userId) {
        // todo
        return true;
    }

    @Override
    public Integer queryRaffleTimesByUserId(Integer userId) {
        // todo
        return 49;
    }
}
