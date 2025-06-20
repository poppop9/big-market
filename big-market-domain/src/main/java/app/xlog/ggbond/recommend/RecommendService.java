package app.xlog.ggbond.recommend;

import app.xlog.ggbond.exception.BigMarketException;
import app.xlog.ggbond.security.model.UserPurchaseHistoryBO;
import app.xlog.ggbond.resp.BigMarketRespCode;
import app.xlog.ggbond.GlobalConstant;
import app.xlog.ggbond.raffle.model.bo.AwardBO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 推荐领域 - 推荐服务
 */
@Service
public class RecommendService {

    @Resource
    private AIRepo aiRepo;

    /**
     * 跟据用户购买历史数据，生成合适的 gpt 问答
     */
    private String generateGptQuestionByUserPurchaseHistory(List<UserPurchaseHistoryBO> userPurchaseHistoryBOList) {
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
     * 跟据海量用户最近的购买历史，生成合适的推荐热销产品的 gpt 问答
     */
    private String generateGptQuestionToHotSaleProduct(List<UserPurchaseHistoryBO> userPurchaseHistoryBOList) {
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
                + "3. 推荐的商品无需一定在海量用户的购买历史中，只需要接近即可 \n"
                + "\n"
                + "以下是海量用户的购买历史：\n"
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
     */
    public List<AwardBO> recommendAwardByUserPurchaseHistory(String role,
                                                             List<UserPurchaseHistoryBO> userPurchaseHistoryBOList) {
        // 购买历史过多要截取
        if (userPurchaseHistoryBOList.size() > 10) {
            userPurchaseHistoryBOList = userPurchaseHistoryBOList.subList(0, 10);
        }

        String question = generateGptQuestionByUserPurchaseHistory(userPurchaseHistoryBOList);
        String answer = aiRepo.syncInvoke(role, question);
        List<AwardBO> awardBOList = Arrays.stream(answer.split("\n")).flatMap(item -> {
                    String[] split = item.split(":");
                    if (split.length != 2)
                        throw new BigMarketException(BigMarketRespCode.AI_RESPONSE_ERROR);

                    GlobalConstant.AwardLevel awardLevel = GlobalConstant.AwardLevel
                            .getNameByPriceRange(split[0].trim());
                    if (awardLevel == null)
                        throw new BigMarketException(BigMarketRespCode.AI_RESPONSE_ERROR);

                    AtomicInteger initAwardSort = new AtomicInteger(awardLevel.getInitAwardSort());
                    return Arrays.stream(split[1].trim().replace("[", "").replace("]", "")
                                    .split(", "))
                            .map(child -> AwardBO.builder()
                                    .awardTitle(child)
                                    .awardSubtitle(awardLevel.getAwardSubtitle())
                                    .awardRate(awardLevel.getAwardRate())
                                    .awardCount(awardLevel.getAwardCount())
                                    .awardSort(initAwardSort.getAndIncrement())
                                    .build());
                })
                .toList();
        return awardBOList;
    }

    /**
     * 跟据海量用户最近的购买历史，推荐热销商品
     */
    public List<AwardBO> recommendHotSaleProductByRecentPurchaseHistory(String role, List<UserPurchaseHistoryBO> recentPurchaseHistoryList) {
        String question = generateGptQuestionToHotSaleProduct(recentPurchaseHistoryList);
        String answer = aiRepo.syncInvoke(role, question);
        return Arrays.stream(answer.split("\n"))
                .flatMap(item -> {
                    String[] split = item.split(":");
                    if (split.length != 2) throw new BigMarketException(BigMarketRespCode.AI_RESPONSE_ERROR);

                    GlobalConstant.AwardLevel awardLevel = GlobalConstant.AwardLevel.getNameByPriceRange(split[0].trim());
                    if (awardLevel == null) throw new BigMarketException(BigMarketRespCode.AI_RESPONSE_ERROR);

                    AtomicInteger initAwardSort = new AtomicInteger(awardLevel.getInitAwardSort());
                    return Arrays.stream(split[1].trim()
                                    .replace("[", "")
                                    .replace("]", "")
                                    .split(", "))
                            .map(child -> AwardBO.builder()
                                    .awardTitle(child)
                                    .awardSubtitle(awardLevel.getAwardSubtitle())
                                    .awardRate(awardLevel.getAwardRate())
                                    .awardCount(awardLevel.getAwardCount())
                                    .awardSort(initAwardSort.getAndIncrement())
                                    .build());
                })
                .toList();
    }

}
