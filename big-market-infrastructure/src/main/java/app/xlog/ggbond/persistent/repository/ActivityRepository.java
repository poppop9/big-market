package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.activity.model.po.ActivityOrderBO;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeConfigBO;
import app.xlog.ggbond.activity.repository.IActivityRepo;
import app.xlog.ggbond.persistent.po.activity.ActivityAccount;
import app.xlog.ggbond.persistent.po.activity.ActivityOrder;
import app.xlog.ggbond.persistent.po.activity.ActivityOrderType;
import app.xlog.ggbond.persistent.po.activity.ActivityOrderTypeConfig;
import app.xlog.ggbond.persistent.repository.jpa.ActivityAccountJpa;
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
    @Autowired
    private ActivityAccountJpa activityAccountJpa;

    /**
     * 插入 - 插入活动单流水
     */
    @Override
    public ActivityOrderBO saveActivityOrder(ActivityOrderBO activityOrderBO) {
        ActivityOrder activityOrder = activityOrderJPA.save(
                BeanUtil.copyProperties(activityOrderBO, ActivityOrder.class)
        );
        return BeanUtil.copyProperties(activityOrder, ActivityOrderBO.class);
    }

    /**
     * 查询 - 根据活动id查询活动单类型配置
     */
    @Override
    public List<ActivityOrderTypeConfigBO> findActivityOrderTypeConfigByActivityId(Long activityId) {
        List<ActivityOrderTypeConfig> byActivityId = activityOrderTypeConfigJpa.findByActivityId(activityId);
        return BeanUtil.copyToList(byActivityId, ActivityOrderTypeConfigBO.class);
    }

    /**
     * 更新 - 增加可用抽奖次数
     */
    @Override
    public void increaseAvailableRaffleTime(Long userId, Long activityId, Long raffleCount) {
        activityAccountJpa.updateAvailableRaffleCountByUserIdAndActivityId(raffleCount, userId, activityId);
    }

    /**
     * 新增 - 初始化活动账户
     */
    @Override
    public void initActivityAccount(Long userId, long activityId) {
        if (!activityAccountJpa.existsByUserIdAndActivityId(userId, activityId)) {
            activityAccountJpa.save(new ActivityAccount(
                    userId, activityId, 0L
            ));
        }
    }

    /**
     * 判断 - 判断今天是否已签到领取AO
     */
    @Override
    public boolean existSignInToClaimAOToday(Long userId, Long activityId) {
        return activityOrderJPA.existsByUserIdAndActivityIdAndActivityOrderTypeName(
                userId,
                activityId,
                ActivityOrderType.ActivityOrderTypeName.SIGN_IN_TO_CLAIM
        );
    }

}
