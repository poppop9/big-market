package app.xlog.ggbond.config;

import cn.hutool.core.util.StrUtil;
import cn.zhxu.bs.DbMapping;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.boot.prop.Sql;
import cn.zhxu.bs.implement.DefaultDbMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanSearcherConfig {

    /**
     * 让BeanSearcher适配JPA
     * @param config
     * @return
     */
    @Bean
    public DbMapping bsJpaDbMapping(BeanSearcherProperties config) {
        var mapping = new DefaultDbMapping() {
            @Override
            public String toTableName(Class<?> beanClass) {
                // 识别 JPA 的 @Table 注解
                var table = beanClass.getAnnotation(jakarta.persistence.Table.class);
                if (table != null && StrUtil.isNotBlank(table.name())) {
                    return table.name();
                }
                // 识别 JPA 的 @Entity 注解
                var entity = beanClass.getAnnotation(jakarta.persistence.Entity.class);
                if (entity != null && StrUtil.isNotBlank(entity.name())) {
                    return entity.name();
                }

                return super.toTableName(beanClass);
            }

            @Override
            public String toColumnName(BeanField field) {
                // 识别 JPA 的 @Column 注解
                var column = field.getAnnotation(jakarta.persistence.Column.class);
                if (column != null && StrUtil.isNotBlank(column.name())) {
                    return column.name();
                }

                return super.toColumnName(field);
            }
        };

        Sql.DefaultMapping conf = config.getSql().getDefaultMapping();
        mapping.setTablePrefix(conf.getTablePrefix());
        mapping.setUpperCase(conf.isUpperCase());
        mapping.setUnderlineCase(conf.isUnderlineCase());
        mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
        mapping.setIgnoreFields(conf.getIgnoreFields());
        mapping.setDefaultInheritType(conf.getInheritType());
        mapping.setDefaultSortType(conf.getSortType());

        return mapping;
    }
}
