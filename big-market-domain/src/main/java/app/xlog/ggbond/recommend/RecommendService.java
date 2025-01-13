package app.xlog.ggbond.recommend;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 推荐领域 - 推荐服务
 */
@Service
public class RecommendService {

    /**
     * 跟据用户的购买历史，推荐合适的奖品
     * <p>
     * todo 未完成
     * todo 后续可以加一个 batch，然后更改 gpt 提示词
     */
    public List<AwardBO> recommendAwardByUserPurchaseHistory(List<UserPurchaseHistoryBO> userPurchaseHistoryBOList) {

        return null;
    }

}
