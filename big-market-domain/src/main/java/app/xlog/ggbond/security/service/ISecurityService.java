package app.xlog.ggbond.security.service;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;

import java.util.List;

/**
 * 安全领域 - 安全服务接口
 * <p>
 * - 抽奖领域
 *      - todo 目前这个抽奖池也不是动态的，最好做到以活动为单位，每个活动的抽奖池配置可以自定义
 *      - todo 刚登录的那些操作可以转为异步，不阻塞登录成功。但是抽奖的时候要判断这些异步操作是否都执行成功了，如果未执行完成，则阻塞抽奖
 *      - todo 如果用户抽奖流程失败了，那么需要将用户的抽奖次数，活动单信息等还原
 *      - todo 如果抽奖报错了，要还原位图
 * - 活动领域
 *      - todo 需要有一张抽奖次数的价格表
 * - 推荐领域
 *      - todo 生成大规模的用户，和历史购买数据
 */
public interface ISecurityService {

    // 登录
    Boolean doLogin(Long userId, String password);

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

}
