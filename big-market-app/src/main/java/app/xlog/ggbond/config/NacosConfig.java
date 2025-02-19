package app.xlog.ggbond.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Nacos 配置
 */
@Configuration
public class NacosConfig {

    @Value("${nacos.serverAddr}")
    private String serverAddr;
    @Value("${nacos.namespace}")
    private String namespace;

    /**
     * 配置中心服务
     */
    @Bean
    public ConfigService configService() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        if (!namespace.isEmpty()) properties.put(PropertyKeyConst.NAMESPACE, namespace);

        return NacosFactory.createConfigService(properties);
    }

    /**
     * 服务发现服务
     */
    @Bean
    public NamingService namingService() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        if (!namespace.isEmpty()) properties.put(PropertyKeyConst.NAMESPACE, namespace);

        return NacosFactory.createNamingService(properties);
    }

}
