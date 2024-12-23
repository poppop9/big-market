package app.xlog.ggbond.raffle;

import app.xlog.ggbond.security.SecurityEvents;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 抽奖领域 - 事件监听器
 */
@Component
public class RaffleEventListeners {

    /**
     * 处理用户登录事件
     */
    @EventListener
    public void handleUserLoginEvent(SecurityEvents.UserLoginEvent userLoginEvent) {
/*        String lotteryInfo = lotteryDomainService.prepareLotteryInfoForUser(userLoginEvent.getUsername());
        redisService.set("lottery_info:" + event.getUsername(), lotteryInfo);*/
    }

}
