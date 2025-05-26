package app.xlog.ggbond.listener;

import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.MQMessage;
import app.xlog.ggbond.raffle.model.vo.DecrQueueVO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleDispatchRepo;
import app.xlog.ggbond.raffle.service.IRaffleDispatch;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 抽奖领域 - 监听器
 */
@Slf4j
@Component
public class RaffleListener {

    @Resource
    private FlowExecutor flowExecutor;
    @Resource
    private IRaffleDispatchRepo raffleDispatchRepo;
    @Resource
    private IRaffleDispatch raffleDispatch;

    /**
     * 消费扣减奖品库存消息
     */
    @KafkaListener(topics = GlobalConstant.KafkaConstant.DECR_AWARD_INVENTORY,
            groupId = GlobalConstant.KafkaConstant.GROUP_ID)
    public void consumeDecrAwardCountMessage(ConsumerRecord<String, MQMessage<DecrQueueVO>> record) {
        DecrQueueVO decrQueueVO = record.value().getData();
        raffleDispatchRepo.updateAwardCount(decrQueueVO);
        log.info("抽奖领域 - 扣减数据库中 {} 策略 {} 奖品的库存成功",
                decrQueueVO.getStrategyId(),
                decrQueueVO.getAwardId()
        );
    }

    /**
     * 消费执行抽奖后置过滤器链消息
     */
    @SneakyThrows
    @KafkaListener(topics = GlobalConstant.KafkaConstant.RAFFLE_AFTER_CHAIN,
            groupId = GlobalConstant.KafkaConstant.GROUP_ID)
    public void consumeRaffleAfterChainMessage(ConsumerRecord<String, MQMessage<RaffleFilterContext>> record) {
        RaffleFilterContext context = record.value().getData();
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("RAFFLE_AFTER_CHAIN", null, context);
        context = liteflowResponse.getContextBean(RaffleFilterContext.class);
        Long userId = context.getUserBO().getUserId();
        if (!liteflowResponse.isSuccess()) {
            log.error("抽奖领域 - " + "用户 " + userId + " 抽奖后置过滤器链异常");
            throw liteflowResponse.getCause();
        }
        raffleDispatch.releaseRaffleLock(userId);
    }

}
