package app.xlog.ggbond.raffle.service.filterChain.filters;

import app.xlog.ggbond.raffle.model.AwardBO;
import app.xlog.ggbond.raffle.model.StrategyBO;
import app.xlog.ggbond.raffle.model.vo.FilterParam;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import app.xlog.ggbond.user.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 前置过滤器链
 */
@Slf4j
@LiteflowComponent
public class RafflePreFilters {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IUserService userService;
    @Resource
    private IRaffleRepository raffleRepository;

    /**
     * 黑名单过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "blacklistRaffleFilter",
            nodeName = "黑名单过滤器")
    public void blacklistRaffleFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);

        // 如果是黑名单用户，拦截
        if (userService.isBlacklistUser(context.getUserId())) {
            log.atInfo().log("抽奖领域 - 黑名单过滤器拦截");
            context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.INTERCEPT);
            context.setDispatchParam(RaffleFilterContext.DispatchParam.BlacklistAward);
        } else {
            log.atInfo().log("抽奖领域 - 黑名单过滤器放行");
            context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.PASS);
        }
    }

    /**
     * 抽奖池匹配过滤器 - 根据用户的抽奖次数，过滤出对应的抽奖池大小
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "matchRafflePoolFilter",
            nodeName = "抽奖池匹配过滤器")
    public void matchRafflePoolFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);

        // 用户的抽奖次数 todo 到时候增加用户抽奖次数时，应该抽奖成功了次数才 +1，因为有可能抽奖会失败
        Long raffleTimes = userService.queryRaffleTimesByUserId(context.getUserId());

        // <++++++++++ 策略规则 ++++++++++>
        try {
            String rules = raffleRepository.findStrategyByStrategyId(context.getStrategyId()).getRules();
            Map<String, Integer> strategyRuleMap = objectMapper.readValue(rules, new TypeReference<Map<String, Integer>>() {
                    })
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() != -1)  // 过滤掉无效的 -1 值
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            // 规则的先后顺序 -> 就是数据库中 rules 里的 json 的先后顺序 -> json里在前，映射出来的map就在前，就会先遍历
            // 根据数据库，动态设定dispatchParam
            for (String key : strategyRuleMap.keySet()) {
                Optional<RaffleFilterContext.DispatchParam> optionalItem = RaffleFilterContext.DispatchParam.isExist(key);
                if (optionalItem.isPresent() && (raffleTimes + 1) == strategyRuleMap.get(key)) {
                    context.setDispatchParam(optionalItem.get());
                    // 结束整个方法
                    return;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // <++++++++++ 奖品规则 ++++++++++>
        List<JsonNode> ruleValues = raffleRepository.queryCommonAwards(context.getStrategyId()).stream()
                .map(AwardBO::getRules)
                .distinct()
                .map(rules -> {
                    try {
                        return objectMapper.readValue(rules, JsonNode.class);  // 这个Map里永远只有一个键值对
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(jsonNode -> jsonNode.asInt() != -1)
                .sorted(Comparator.comparingInt(JsonNode::asInt))
                .toList();
        log.atInfo().log("奖品规则 - ruleValues: {}", ruleValues);

        // <++++++++++ 特殊规则 ++++++++++>
        for (JsonNode ruleValue : ruleValues) {
            if (raffleTimes < ruleValue.asInt()) {
                // 动态获取枚举实例
                context.setDispatchParam(RaffleFilterContext.DispatchParam.valueOf(
                        ruleValue.fieldNames().next()
                ));
                return;
            }
        }

        // <++++++++++ 兜底规则 ++++++++++> : 其他奖品规则都不符合，那抽奖池就是所有奖品
        context.setDispatchParam(RaffleFilterContext.DispatchParam.CommonAwards);
    }

}
