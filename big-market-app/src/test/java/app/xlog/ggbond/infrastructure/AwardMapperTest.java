package app.xlog.ggbond.infrastructure;

import app.xlog.ggbond.persistent.mapper.AwardMapper;
import app.xlog.ggbond.persistent.po.Award;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AwardMapperTest {
    @Autowired
    private AwardMapper awardMapper;

    // 查询所有奖品
    @Test
    public void testSelectAll() {
        List<Award> awards = awardMapper.selectList(null);
        awards.forEach(System.out::println);
    }
}
