package app.xlog.ggbond.infrastructure;

import cn.hutool.core.util.StrUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HutoolTest {
    @Test
    public void test_StrUtil() {
        String[] str = {"   ", "   ", "111", "&&&"};
        System.out.println(StrUtil.hasBlank(str));
    }
}
