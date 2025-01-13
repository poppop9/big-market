package app.xlog.ggbond.persistent.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为实体的字段提供默认的生成逻辑
 *
 * 示例：
 * - @JpaDefaultValue(howToCreate = "cn.hutool.core.util.IdUtil.getSnowflakeNextId()")
 * - private Long activityOrderId;
 * - @JpaDefaultValue(howToCreate = "java.time.LocalDateTime.of(2000, 12, 31, 0, 0, 0)")
 * - private LocalDateTime activityOrderEffectiveTime;
 */
@Target(ElementType.FIELD)  // 只能应用于字段
@Retention(RetentionPolicy.RUNTIME)  // 在运行时保留
public @interface JpaDefaultValue {
    String howToCreate();  // 指定默认值生成方法
}