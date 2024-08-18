package app.xlog.ggbond.config;

import app.xlog.ggbond.strategy.service.filter.BlacklistRaffleFilter;
import app.xlog.ggbond.strategy.service.filter.InventoryFilter;
import app.xlog.ggbond.strategy.service.filter.RaffleFilterChain;
import app.xlog.ggbond.strategy.service.filter.RaffleTimesRaffleFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterChainConfig {

    @Bean
    public RaffleFilterChain filterChain() {
        // 在这里构建过滤器链
        return new RaffleFilterChain()
                // 前置过滤器
                .addPreFilter(new BlacklistRaffleFilter())
                .addPreFilter(new RaffleTimesRaffleFilter())
                // 后置过滤器
                .addAfterFilter(new InventoryFilter());
    }
}
