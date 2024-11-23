package app.xlog.ggbond.raffle.service.filterChain.filters;

import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleRepository;
import app.xlog.ggbond.security.service.ISecurityService;
import cn.hutool.core.lang.Range;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 前置过滤器链
 */
@Slf4j
@LiteflowComponent
public class RafflePreFilters {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ISecurityService securityService;
    @Resource
    private IRaffleRepository raffleRepository;

    /**
     * 黑名单过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "BlacklistRaffleFilter",
            nodeName = "黑名单过滤器")
    public void blacklistRaffleFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);

        // 如果是黑名单用户，拦截
        if (securityService.isBlacklistUser()) {
            log.atInfo().log("抽奖领域 - 黑名单过滤器拦截");
            context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.INTERCEPT);
            context.setDispatchParam(RaffleFilterContext.DispatchParam.BlacklistPool);
        } else {
            log.atInfo().log("抽奖领域 - 黑名单过滤器放行");
        }
    }

    /**
     * 特殊次数抽奖池匹配过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "SpecialTimeMatchRafflePoolFilter",
            nodeName = "特殊次数抽奖池匹配过滤器")
    public void specialTimeMatchRafflePoolFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        // 用户的抽奖次数
        Long raffleTimes = securityService.queryRaffleTimesByUserId(context.getUserId());

        // 所有的特殊次数抽奖池
        Map<Long, String> timeNameMap = raffleRepository.findAllRafflePoolByStrategyId(context.getStrategyId()).stream()
                .filter(item -> item.getRafflePoolType() == RafflePoolBO.RafflePoolType.SpecialTime)
                .collect(Collectors.toMap(
                        RafflePoolBO::getSpecialTimeValue,
                        RafflePoolBO::getRafflePoolName
                ));

        if (timeNameMap.containsKey(raffleTimes + 1L)) {
            log.atInfo().log("抽奖领域 - 特殊次数抽奖池匹配过滤器拦截");
            context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.INTERCEPT);
            context.setDispatchParam(
                    RaffleFilterContext.DispatchParam.valueOf(timeNameMap.get(raffleTimes + 1L))
            );
        } else {
            log.atInfo().log("抽奖领域 - 特殊次数抽奖池匹配过滤器放行");
        }
    }

    /**
     * 普通次数抽奖池匹配过滤器 - 根据用户的抽奖次数，过滤出对应的抽奖池大小
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "NormalTimeMatchRafflePoolFilter",
            nodeName = "普通次数抽奖池匹配过滤器")
    public void normalTimeMatchRafflePoolFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        // 用户的抽奖次数 todo 增加用户抽奖次数时，应该抽奖成功了次数才 +1，因为有可能抽奖会失败
        Long raffleTimes = securityService.queryRaffleTimesByUserId(context.getUserId());

        // 所有的普通次数抽奖池
        Map<List<Long>, String> rangeNameMap = raffleRepository.findAllRafflePoolByStrategyId(context.getStrategyId()).stream()
                .filter(item -> item.getRafflePoolType() == RafflePoolBO.RafflePoolType.NormalTime)
                .collect(Collectors.toMap(
                        item -> List.of(item.getNormalTimeStartValue(), item.getNormalTimeEndValue()),
                        RafflePoolBO::getRafflePoolName
                ));

        String rafflePoolName = rangeNameMap.entrySet().stream()
                .filter(item -> raffleTimes >= item.getKey().get(0) && raffleTimes <= item.getKey().get(1))
                .map(Map.Entry::getValue)
                .findFirst()
                .get();

        log.atInfo().log("抽奖领域 - 普通次数抽奖池匹配过滤器拦截，调度到 " + rafflePoolName + " 抽奖池");
        context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.INTERCEPT);
        context.setDispatchParam(RaffleFilterContext.DispatchParam.valueOf(rafflePoolName));
    }

}
