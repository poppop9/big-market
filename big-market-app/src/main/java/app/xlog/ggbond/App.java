package app.xlog.ggbond;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * - 抽奖领域
 *      - todo 目前这个抽奖池也不是动态的，最好做到以活动为单位，每个活动的抽奖池配置可以自定义
 *      - todo 中奖流水中需要记录活动单id
 *      - todo redis中的黑名单用户要定时更新
 *      - todo 什么行为算是违规行为，要列为黑名单用户：
 *          - 使用插件、或者脚本模拟用户行为
 *          - 对于同一ip的异常高流量，要做限流，并加入黑名单
 * - 发奖领域
 *      - todo 引入积分概念，抽到奖品随机积分可以充值到积分账户中，积分达到一定的次数可以兑换奖品
 * - 安全领域
 * - 活动领域
 *      - todo 可以弄一个后台，配置Activity
 *      - todo 用户可以手动取消活动单
 * - 推荐领域
 *      - todo 生成大规模的用户，和历史购买数据
 */
@EnableDubbo
@EnableAsync
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
