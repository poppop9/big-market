package app.xlog.ggbond.recommend;

import app.xlog.ggbond.raffle.model.bo.AwardBO;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐领域 - 推荐服务
 */
@Service
public class RecommendService {

    /**
     * 跟据用户购买历史数据，生成合适的 gpt 问答
     */
    public String generateGptQuestionByUserPurchaseHistory(List<UserPurchaseHistoryBO> userPurchaseHistoryBOList) {
        String data = userPurchaseHistoryBOList.stream()
                .map(UserPurchaseHistoryBO::toString)
                .collect(Collectors.joining("\n"))
                .replace("UserPurchaseHistoryBO", "");
        for (Field field : UserPurchaseHistoryBO.class.getDeclaredFields()) {
            String chinese = UserPurchaseHistoryBO.FieldName.getChinese(field.getName());
            data = data.replace(field.getName(), chinese);
        }

        String question = "你的推荐要求如下: \n"
                + "1. 一共推荐8个商品，分为3个级别（价格低的商品4个，价格稍高的商品3个，价格高的商品1个） \n"
                + "2. 价格低的商品价格在0-50元之间，价格稍高的商品价格在50-200元之间，价格高的商品价格在200-2000元之间 \n"
                + "\n"
                + "以下是该用户的购买历史：\n"
                + data
                + "\n\n"
                + "回答简洁不要多余的文字符号，格式是：\n"
                + "0-50 : [商品1, 商品2, 商品3, 商品4]\n"
                + "50-200 : [商品5, 商品6, 商品7]\n"
                + "200-2000 : [商品8]\n";
        return question;
    }

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
