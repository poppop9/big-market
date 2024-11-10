package app.xlog.ggbond.raffle.service.filter;

import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.model.StrategyBO;
import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import app.xlog.ggbond.raffle.utils.SpringContextUtil;
import app.xlog.ggbond.user.service.IUserService;
import app.xlog.ggbond.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// 这一定是最后一个前置过滤器，所以不用拦截
@Slf4j
public class RaffleTimesRaffleFilter implements RaffleFilter {

    private final IUserService userService = new UserService();
    private final IRaffleRepository raffleRepository;
    private final ObjectMapper objectMapper;

    public RaffleTimesRaffleFilter() {
        raffleRepository = SpringContextUtil.getBean(IRaffleRepository.class);
        objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
    }

    /**
     * 根据用户的抽奖次数，过滤出对应的抽奖池大小
     **/
    @Override
    public FilterParam filter(FilterParam filterParam) {
        // 用户的抽奖次数 todo 到时候增加用户抽奖次数时，应该抽奖成功了次数才 +1，因为有可能抽奖会失败
        Long raffleTimes = userService.queryRaffleTimesByUserId(filterParam.getUserId());

        /*
          策略规则 - 优先
         */
        StrategyBO strategyBO = raffleRepository.queryStrategys(filterParam.getStrategyId());
        try {
            Map<String, Integer> strategyRuleMap = objectMapper.readValue(
                            strategyBO.getRules(),
                            new TypeReference<Map<String, Integer>>() {
                            }
                    )
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() != -1)  // 过滤掉无效的 -1 值
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // 规则的先后顺序 -> 就是数据库中 rules 里的 json 的先后顺序 -> json里在前，映射出来的map就在前，就会先遍历
            // 根据数据库，动态设定dispatchParam
            for (String key : strategyRuleMap.keySet()) {
                Optional<FilterParam.DispatchParam> optional = FilterParam.DispatchParam.isExist(key);
                if (optional.isPresent()) {
                    if ((raffleTimes + 1) == strategyRuleMap.get(key)) {
                        filterParam.setDispatchParam(optional.get());
                        return filterParam;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        /*
         * 奖品规则
         */
        List<AwardBO> awardBOS = raffleRepository.queryCommonAwards(filterParam.getStrategyId());
        List<JsonNode> ruleValues = awardBOS.stream()
                .map(AwardBO::getRules)
                .distinct()   // 去除重复
                .map(rules -> {
                    try {
                        // 这个Map里永远只有一个键值对
                        return objectMapper.readValue(rules, JsonNode.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(jsonNode -> jsonNode.asInt() != -1)
                .sorted(Comparator.comparingInt(JsonNode::asInt))
                .toList();
        log.atInfo().log("奖品规则 - ruleValues: {}", ruleValues);

        // 特殊规则
        for (JsonNode ruleValue : ruleValues) {
            if (raffleTimes < ruleValue.asInt()) {
                // 动态获取枚举实例
                FilterParam.DispatchParam dispatchParam = FilterParam.DispatchParam.valueOf(ruleValue.fieldNames().next());
                filterParam.setDispatchParam(dispatchParam);

                return filterParam;
            }
        }

        // 其他奖品规则都不符合，都没拦截到，那抽奖池就是所有奖品
        filterParam.setDispatchParam(FilterParam.DispatchParam.CommonAwards);
        return filterParam;
    }
}
