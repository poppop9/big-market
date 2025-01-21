package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeConfigBO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.persistent.po.activity.ActivityOrder;
import app.xlog.ggbond.persistent.po.activity.ActivityOrderTypeConfig;
import app.xlog.ggbond.persistent.repository.jpa.ActivityOrderJpa;
import app.xlog.ggbond.persistent.repository.jpa.ActivityOrderTypeConfigJpa;
import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 活动领域 - 活动仓储实现类
 */
@Repository
public class ActivityRepository implements IActivityRepo {

    @Resource
    private ActivityOrderJpa activityOrderJPA;
    @Resource
    private ActivityOrderTypeConfigJpa activityOrderTypeConfigJpa;

    /**
     * 插入 - 插入活动单流水
     */
    @Override
    public void saveActivityOrder(ActivityOrderBO activityOrderBO) {
        activityOrderJPA.save(
                BeanUtil.copyProperties(activityOrderBO, ActivityOrder.class)
        );
    }

    /**
     * 查询 - 根据活动id查询活动单类型配置
     */
    @Override
    public List<ActivityOrderTypeConfigBO> findActivityOrderTypeConfigByActivityId(Long activityId) {
        List<ActivityOrderTypeConfig> byActivityId = activityOrderTypeConfigJpa.findByActivityId(activityId);
        return BeanUtil.copyToList(byActivityId, ActivityOrderTypeConfigBO.class);
    }

}
