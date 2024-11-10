package app.xlog.ggbond.gpt;

import com.zhipu.oapi.ClientV4;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GLMClientV4Config {

    @Value("${glm.apiKey}")
    private String apiKey;

    @Bean
    public ClientV4 clientV4() {
        return new ClientV4.Builder(apiKey)
                .enableTokenCache()  // 缓存token
                .build();
    }
}
