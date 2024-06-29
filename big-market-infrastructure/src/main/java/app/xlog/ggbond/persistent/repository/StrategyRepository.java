package app.xlog.ggbond.persistent.repository;

import app.xlog.ggbond.persistent.mapper.AwardMapper;
import app.xlog.ggbond.persistent.po.Award;
import app.xlog.ggbond.strategy.model.AwardBO;
import app.xlog.ggbond.strategy.repository.IStrategyRepository;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
策略仓库实现类
 */
@Repository
public class StrategyRepository implements IStrategyRepository {
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AwardMapper awardMapper;

    @Override
    public List<AwardBO> queryAwards(int strategyId) {
        String cacheKey = "strategy_awards_" + strategyId;

        // Redis缓存中存在则直接返回
        RList<AwardBO> rList = redissonClient.getList(cacheKey);
        if (!rList.isEmpty() && rList != null) {
            return rList;
        }

        // Redis缓存中不存在则查询数据库
        QueryWrapper<Award> queryWrapper = new QueryWrapper<Award>()
                .eq("strategy_id", strategyId);

        List<Award> awardPOs = awardMapper.selectList(queryWrapper);
        // 将PO转换为BO
        List<AwardBO> awardBOS = BeanUtil.copyToList(awardPOs, AwardBO.class);

        // 将查询结果存入Redis缓存
        rList.addAll(awardBOS);

        return awardBOS;
    }
}
