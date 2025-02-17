package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;

import java.util.List;

/**
 * 安全领域 - 安全服务接口
 * <p>
 * - 抽奖领域
 *      - todo 目前这个抽奖池也不是动态的，最好做到以活动为单位，每个活动的抽奖池配置可以自定义
 *      - todo 中奖流水中需要记录活动单id
 *      - todo 测试抽奖50次是否能中大奖
 *      - todo 页面没有展示当前已经抽奖的次数 ：第一次渲染页面请求接口，后续的渲染跟据抽奖接口返回的抽奖次数来
 *      - todo 抽奖10次锁没生效，抽奖了8次，也可以抽到锁奖品
 * - 奖品发放领域
 * - 活动领域
 *      - todo 将充值活动单的流程加入mq，task处理充值
 * - 推荐领域
 *      - todo 生成大规模的用户，和历史购买数据
 */
public interface ISecurityService {

    // 登录
    Boolean doLogin(Long userId, String password);

    // 跟据 token 退出登录
    void logoutByToken(String token);

    // 查询 - 获取当前登录用户id
    Long getLoginIdDefaultNull();

    // 查询 - 判断用户是否是黑名单用户
    Boolean isBlacklistUser(Long userId);

    // 查询 - 查询出所有的黑名单用户
    List<UserBO> queryAllBlacklistUser();

    // 查询 - 跟据userId，查询当前用户
    UserBO findUserByUserId(Long userId);

    // 查询 - 查询用户购买历史
    List<UserPurchaseHistoryBO> findUserPurchaseHistory(Long userId);

    // 查询 - 查询最近的购买历史
    List<UserPurchaseHistoryBO> findRecentPurchaseHistory();

    // 插入 - 将黑名单用户放入布隆过滤器
    void insertBlacklistUserListToBloomFilter(List<Long> userIds);

    // 插入 - 将当前用户的角色信息放入session
    void insertPermissionIntoSession();

    // 判断 - 检查该用户是否有策略
    boolean existsByUserIdAndActivityId(Long activityId, Long userId);

    // 判断 - 判断用户是否有购买历史
    boolean existsUserPurchaseHistory(Long userId);

    // 查询 - 查询登录用户的信息
    UserBO findLoginUserInfo();

}
