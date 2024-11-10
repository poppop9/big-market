package app.xlog.ggbond.raffle.model.vo;

import lombok.*;

import java.util.Arrays;
import java.util.Optional;


// 过滤时的参数，包括了过滤要传入的参数和过滤后的结果，抽奖调度根据这个参数来决定抽奖的策略
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterParam {

    /**
     * 过滤传入的参数
     **/
    private MiddleFilterParam middleFilterParam = MiddleFilterParam.PASS;
    // 用户id，需要判断是否是黑名单用户，还要判断用户的抽奖次数
    private Long UserId;
    // 策略id，需要根据策略id来判断到底是哪些奖品
    private Long StrategyId;

    /**
     * 过滤结果
     **/
    private DispatchParam dispatchParam;
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

    @Getter
    public enum DispatchParam {
        /*
        前置过滤器的拦截值
         */
        CommonAwards("rule_common", "101-109 的所有奖品"),
        LockAwards("rule_lock", "除去锁定奖品，101-105 的所有奖品"),
        LockLongAwards("rule_lock_long", "除去最后一个奖品，101-108 的所有奖品"),
        BlacklistAward("rule_blacklist", "黑名单用户的最次奖品"),
        GrandAward("rule_grand", "大奖池，106-108奖品");

        /*
        后置过滤器的拦截值
         */
        private final String code;
        private final String info;

        DispatchParam(String code, String info) {
            this.code = code;
            this.info = info;
        }

        public static Optional<DispatchParam> isExist(String code) {
            return Arrays.stream(DispatchParam.values())
                    .filter(item -> item.getCode().equals(code))
                    .findFirst();
        }
    }
}
