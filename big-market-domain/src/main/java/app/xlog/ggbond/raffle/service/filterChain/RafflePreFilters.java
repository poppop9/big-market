package app.xlog.ggbond.raffle.service.filterChain;

import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.raffle.model.bo.RafflePoolBO;
import app.xlog.ggbond.raffle.model.bo.UserBO;
import app.xlog.ggbond.raffle.model.vo.RaffleFilterContext;
import app.xlog.ggbond.raffle.repository.IRaffleArmoryRepo;
import app.xlog.ggbond.resp.BigMarketRespCode;
import cn.dev33.satoken.session.SaSession;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 抽奖领域 - 前置过滤器链
 */
@Slf4j
@LiteflowComponent
public class RafflePreFilters {

    @Resource
    private IRaffleArmoryRepo raffleArmoryRepo;

    /**
     * 抽奖资格验证过滤器 - 判断登录后为抽奖做准备的各种异步操作是否完成
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "RaffleQualificationFilter",
            nodeName = "抽奖资格验证过滤器")
    public void raffleQualificationFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        UserBO userBO = context.getUserBO();
        SaSession saSession = context.getSaSession();

        log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 抽奖资格验证过滤器 start");
        if (Boolean.parseBoolean(saSession.get("isNewUser").toString())) {
            CompletableFuture<Boolean> doLoginCompletableFuture = (CompletableFuture<Boolean>) saSession.get("doLoginCompletableFuture");
            if (doLoginCompletableFuture.get()) {
                log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 抽奖资格验证过滤器 end");
            } else {
                throw new BigMarketException(BigMarketRespCode.RAFFLE_CONFIG_ARMORY_ERROR);
            }
        }
    }

    /**
     * 数据准备过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "DataPreparationFilter",
            nodeName = "数据准备过滤器")
    public void dataPreparationFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        UserBO userBO = context.getUserBO();

        log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 数据准备过滤器 start");
        Long strategyId = raffleArmoryRepo.findStrategyIdByActivityIdAndUserId(context.getActivityId(), context.getUserBO().getUserId());
        context.setStrategyId(strategyId);
        Long raffleTime = raffleArmoryRepo.queryRaffleTimesByUserId(userBO.getUserId(), context.getStrategyId());
        userBO.setRaffleTime(raffleTime);
        log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 数据准备过滤器 end");
    }

    /**
     * 黑名单过滤器
     */
    @LiteflowMethod(nodeType = NodeTypeEnum.COMMON,
            value = LiteFlowMethodEnum.PROCESS,
            nodeId = "BlacklistRaffleFilter",
            nodeName = "黑名单过滤器")
    public void blacklistRaffleFilter(NodeComponent bindCmp) {
        RaffleFilterContext context = bindCmp.getContextBean(RaffleFilterContext.class);
        UserBO userBO = context.getUserBO();

        // 如果是黑名单用户，拦截
        if (userBO.isBlacklistUser()) {
            log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 黑名单过滤器 start");
            context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.INTERCEPT);
            context.setDispatchParam(RaffleFilterContext.DispatchParam.BlacklistPool);
        } else {
            log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 黑名单过滤器 end");
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
        UserBO userBO = context.getUserBO();
        Long raffleTime = userBO.getRaffleTime();

        log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 特殊次数抽奖池匹配过滤器 start");
        // 所有的特殊次数抽奖池
        Map<Long, String> timeNameMap = raffleArmoryRepo.findAllRafflePoolByStrategyId(context.getStrategyId()).stream()
                .filter(item -> item.getRafflePoolType() == RafflePoolBO.RafflePoolType.SpecialTime)
                .collect(Collectors.toMap(
                        RafflePoolBO::getSpecialTimeValue,
                        RafflePoolBO::getRafflePoolName
                ));
        if (timeNameMap.containsKey(raffleTime + 1L)) {
            context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.INTERCEPT);
            context.setDispatchParam(
                    RaffleFilterContext.DispatchParam.valueOf(timeNameMap.get(raffleTime + 1L))
            );
        } else {
            log.atInfo().log("抽奖领域 - " + userBO.getUserId() + " 特殊次数抽奖池匹配过滤器 end");
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
        Long raffleTime = context.getUserBO().getRaffleTime();  // 用户的抽奖次数

        // 所有的普通次数抽奖池
        Map<List<Long>, String> rangeNameMap = raffleArmoryRepo.findAllRafflePoolByStrategyId(context.getStrategyId()).stream()
                .filter(item -> item.getRafflePoolType() == RafflePoolBO.RafflePoolType.NormalTime)
                .collect(Collectors.toMap(
                        // 开始值和结束值作为key，抽奖池名称作为value
                        item -> List.of(item.getNormalTimeStartValue(), item.getNormalTimeEndValue()),
                        RafflePoolBO::getRafflePoolName
                ));

        String rafflePoolName = rangeNameMap.entrySet().stream()
                .filter(item -> raffleTime >= item.getKey().get(0) && raffleTime <= item.getKey().get(1))
                .map(Map.Entry::getValue)
                .findFirst()
                .get();

        log.atInfo().log("抽奖领域 - " + context.getUserBO().getUserId() + " 普通次数抽奖池匹配过滤器 start，调度到 " + rafflePoolName + " 抽奖池");
        context.setMiddleFilterParam(RaffleFilterContext.MiddleFilterParam.INTERCEPT);
        context.setDispatchParam(RaffleFilterContext.DispatchParam.valueOf(rafflePoolName));
        log.atInfo().log("抽奖领域 - " + context.getUserBO().getUserId() + " 普通次数抽奖池匹配过滤器 end");
    }

}
