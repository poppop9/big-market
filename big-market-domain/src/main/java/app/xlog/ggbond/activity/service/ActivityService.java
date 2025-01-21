package app.xlog.ggbond.activity.service;

import app.xlog.ggbond.activity.repository.IActivityRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 活动领域 - 活动服务
 */
@Service
public class ActivityService implements IActivityService {

    @Resource
    private IActivityRepo activityRepo;

    /**
     * 新增 - 初始化活动账户
     */
    @Override
    public void initActivityAccount(Long userId, long activityId) {
        activityRepo.initActivityAccount(userId, activityId);
    }

}
