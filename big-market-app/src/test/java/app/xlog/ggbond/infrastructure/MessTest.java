package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.po.Award;
import app.xlog.ggbond.strategy.model.AwardBO;
import cn.hutool.core.bean.BeanUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
public class MessTest {
    // 测试hutool的BeanUtil.copyProperties方法
    @Test
    public void testHutoolCopyProperties() {
        Award award = Award.builder()
                .id(1)
                .strategyId(1)
                .awardId(101)
                .awardKey("random_points")
                .awardConfig("random_points_config")
                .awardTitle("随机积分")
                .awardSubtitle("随机积分副标题")
                .awardCount(100)
                .awardRate(0.1f)
                .awardSort(1)
                .rules("rules")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        AwardBO awardBO = new AwardBO();

        BeanUtil.copyProperties(award, awardBO);
        System.out.println(awardBO);
    }

    // 测试hutool的BeanUtil的copyToList方法
    @Test
    public void testcopyToList() {
        List<Award> awards = Stream.of(
                Award.builder()
                        .id(1).strategyId(1).awardId(101)
                        .awardKey("random").awardConfig("random")
                        .awardTitle("随机积分").awardSubtitle("副标题")
                        .awardCount(100).awardRate(0.1f).awardSort(1)
                        .rules("rules").createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build(),
                Award.builder()
                        .id(2).strategyId(1).awardId(102)
                        .awardKey("random").awardConfig("config")
                        .awardTitle("随机积分").awardSubtitle("副")
                        .awardCount(100).awardRate(0.1f)
                        .awardSort(1).rules("rules")
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build()
        ).toList();

        List<AwardBO> awardBOs = BeanUtil.copyToList(awards, AwardBO.class);
        awardBOs.forEach(System.out::println);
    }
}
