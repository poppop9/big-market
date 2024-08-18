package app.xlog.ggbond.config;

import app.xlog.ggbond.strategy.service.filter.BlacklistRaffleFilter;
import app.xlog.ggbond.strategy.service.filter.InventoryFilter;
import app.xlog.ggbond.strategy.service.filter.RaffleFilterChain;
import app.xlog.ggbond.strategy.service.filter.RaffleTimesRaffleFilter;
import jakarta.servlet.FilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterChainConfig {

    @Bean
    public RaffleFilterChain filterChain() {
        // 在这里构建过滤器链
        return new RaffleFilterChain()
                .addPreFilter(new BlacklistRaffleFilter())
                .addPreFilter(new RaffleTimesRaffleFilter())
                .addPreFilter(new InventoryFilter());
    }
}
