package app.xlog.ggbond.infrastructure;

import cn.hutool.core.date.*;
import cn.hutool.core.util.StrUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@SpringBootTest
public class HutoolTest {
    @Test
    public void test_StrUtil() {
        String[] str = {"   ", "   ", "111", "&&&"};
        System.out.println(StrUtil.hasBlank(str));
    }

    @Test
    public void test_Time() {
        LocalDateTime localDateTime1 = LocalDateTimeUtil.parse("2020-01-23T12:23:56");
        System.out.println(localDateTime1);
        LocalDateTime localDateTime2 = LocalDateTimeUtil.parse("2020-01-23 12:23:56", DatePattern.NORM_DATETIME_FORMATTER);
        System.out.println(localDateTime2);
    }
}
