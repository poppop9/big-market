package app.xlog.ggbond.raffle.model.vo;

import app.xlog.ggbond.raffle.model.bo.UserBO;
import cn.dev33.satoken.session.SaSession;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Optional;

/**
 * 抽奖领域 - 抽奖过滤器上下文
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFilterContext {
    // 过滤初始传参
    @Builder.Default
    private RaffleFilterContext.MiddleFilterParam middleFilterParam = RaffleFilterContext.MiddleFilterParam.PASS;
    private UserBO userBO;  // 用户
    private Long activityId;  // 活动id
    private Long strategyId;  // 策略id，需要根据策略id来判断到底是哪些奖品
    private SaSession saSession;  // session

    // 过滤结果出参
    private RaffleFilterContext.DispatchParam dispatchParam;
    private Long awardId;
    private Long userRaffleHistoryId;  // 用户抽奖历史id

    // 每一个过滤器的返回值
    @Getter
    @AllArgsConstructor
    public enum MiddleFilterParam {
        PASS("放行，执行后续流程"),
        INTERCEPT("拦截，不执行后续流程");

        private final String info;
    }

    // 过滤器的调度值
    @Getter
    @AllArgsConstructor
    public enum DispatchParam {
        AllAwardPool("rule_common", "所有奖品"),  // NormalTime
        No1stAwardPool("rule_lock", "没有 109 大奖"),  // NormalTime
        No1stAnd2ndAwardPool("rule_blacklist", "没有 105，106，107，108，109 大奖"),  // NormalTime
        IstAnd2ndAwardPool("rule_grand", "都是一二级的大奖"),  // SpecialTime
        BlacklistPool("rule_blacklist", "黑名单用户");  // SpecialRule

        private final String code;
        private final String info;

        public static Optional<RaffleFilterContext.DispatchParam> isExist(String code) {
            return Arrays.stream(RaffleFilterContext.DispatchParam.values())
                    .filter(item -> item.getCode().equals(code))
                    .findFirst();
        }
    }

}
