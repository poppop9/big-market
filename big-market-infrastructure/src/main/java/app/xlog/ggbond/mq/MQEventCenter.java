package app.xlog.ggbond.mq;

import app.xlog.ggbond.MQMessage;

/**
 * MQ 事件中心
 */
public interface MQEventCenter {

    // 发送消息
    boolean sendMessage(String topic, MQMessage<?> MQMessage);

}
