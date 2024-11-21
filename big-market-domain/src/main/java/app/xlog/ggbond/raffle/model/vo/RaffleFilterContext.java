package app.xlog.ggbond.raffle.model.vo;

import lombok.*;

import java.util.Arrays;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFilterContext{

    /**
     * 过滤初始传参
     **/
    private RaffleFilterContext.MiddleFilterParam middleFilterParam = RaffleFilterContext.MiddleFilterParam.PASS;
    // 用户id，需要判断是否是黑名单用户，还要判断用户的抽奖次数
    private Long userId;
    // 策略id，需要根据策略id来判断到底是哪些奖品
    private Long strategyId;

    /**
     * 过滤结果出参
     **/
    private RaffleFilterContext.DispatchParam dispatchParam;
    private Long awardId;

    // 每一个过滤器的返回值
    @Getter
    public enum MiddleFilterParam {
        PASS("200", "放行，执行后续流程"),
        INTERCEPT("400", "拦截，不执行后续流程");

        private final String code;
        private final String info;

        MiddleFilterParam(String code, String info) {
            this.code = code;
            this.info = info;
        }
    }

    // 最后过滤器的调度值
    @Getter
    public enum DispatchParam {
        // todo 随即积分没有必要设置黑名单规则，也不需要lock_long规则，用value值区分就好了
        // <++++++++++ 前置过滤器的拦截值 ++++++++++>
        CommonAwards("rule_common", "该策略里的所有奖品"),
        LockAwards("rule_lock", "除去锁定奖品的所有奖品"),
        BlacklistAward("rule_blacklist", "黑名单用户的最次奖品"),
        GrandAward("rule_grand", "大奖池，106-108奖品");

        private final String code;
        private final String info;

        DispatchParam(String code, String info) {
            this.code = code;
            this.info = info;
        }

        public static Optional<RaffleFilterContext.DispatchParam> isExist(String code) {
            return Arrays.stream(RaffleFilterContext.DispatchParam.values())
                    .filter(item -> item.getCode().equals(code))
                    .findFirst();
        }
    }

}
