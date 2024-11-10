package app.xlog.ggbond.persistent.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
//    @Value("${SecretKey.redis.password}")
//    private String redisPassword;

    @Value("${redis.address}")
    private String redisAddress;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        //设置redis的地址，这里是单机模式
        config.useSingleServer()
//                .setPassword(redisPassword)
                .setAddress(redisAddress)
                // 设置连接池的大小，默认为64
                .setConnectionPoolSize(64)
                // 设置连接池的最小空闲连接数，默认为10
                .setConnectionMinimumIdleSize(10)
                // 设置连接的最大空闲时间（单位：毫秒），超过该时间的空闲连接将被关闭，默认为10000
                .setIdleConnectionTimeout(10000)
                // 设置连接超时时间（单位：毫秒），默认为10000
                .setConnectTimeout(10000)
                // 设置连接重试次数，默认为3
                .setRetryAttempts(3)
                // 设置连接重试的间隔时间（单位：毫秒），默认为1000
                .setRetryInterval(1000)
                // 设置定期检查连接是否可用的时间间隔（单位：毫秒），默认为0，表示不进行定期检查
                .setPingConnectionInterval(0)
                // 设置是否保持长连接，默认为true
                .setKeepAlive(true);

        // 设置Redisson存储数据的格式，这里使用Json，一定要配置，防止乱码
        config.setCodec(new JsonJacksonCodec());

        return Redisson.create(config);
    }
}
