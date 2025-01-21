package app.xlog.ggbond.http;

import app.xlog.ggbond.IActivityApiService;
import app.xlog.ggbond.activity.model.po.ActivityOrderTypeBO;
import app.xlog.ggbond.activity.model.vo.AOContext;
import app.xlog.ggbond.activity.service.statusFlow.AOEventCenter;
import app.xlog.ggbond.integrationService.TriggerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 活动领域
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityController implements IActivityApiService {

    @Resource
    private TriggerService triggerService;

    /**
     * 活动领域 - 充值活动单
     *
     * @param activityId 活动id
     * @param activityOrderTypeName 活动单类型名称
     */
    @PostMapping("/v1/rechargeActivityOrder")
    public void rechargeActivityOrder(@RequestParam Long activityId,
                                      @RequestParam String activityOrderTypeName) {
        triggerService.rechargeActivityOrder(activityId, activityOrderTypeName);
    }

}
