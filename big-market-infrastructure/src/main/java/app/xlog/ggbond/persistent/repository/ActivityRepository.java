package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.activity.model.ActivityOrderFlowBO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.persistent.po.activity.ActivityOrderFlow;
import app.xlog.ggbond.persistent.repository.jpa.ActivityOrderFlowJpa;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

/**
 * 活动领域 - 活动仓储实现类
 */
@Repository
public class ActivityRepository implements IActivityRepo {

    @Resource
    private ActivityOrderFlowJpa activityOrderFlowJPA;

    /**
     * 保存活动单流水
     */
    @Override
    public void saveActivityOrderFlow(ActivityOrderFlowBO activityOrderFlowBO) {
        activityOrderFlowJPA.save(
                BeanUtil.copyProperties(activityOrderFlowBO, ActivityOrderFlow.class)
        );
    }

}
