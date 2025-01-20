package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.activity.model.ActivityOrderBO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.persistent.po.activity.ActivityOrder;
import app.xlog.ggbond.persistent.repository.jpa.ActivityOrderJpa;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * 活动领域 - 活动仓储实现类
 */
@Repository
public class ActivityRepository implements IActivityRepo {

    @Resource
    private ActivityOrderJpa activityOrderJPA;

    /**
     * 保存活动单流水
     */
    @Override
    public void saveActivityOrderFlow(ActivityOrderBO activityOrderBO) {
        activityOrderJPA.save(
                BeanUtil.copyProperties(activityOrderBO, ActivityOrder.class)
        );
    }

}
