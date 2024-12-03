package app.xlog.ggbond.job;

import app.xlog.ggbond.security.service.ISecurityService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 项目初始化任务
 */
@Slf4j
@Component
public class InitializeJob {

    @Resource
    private ISecurityService securityService;

    @PostConstruct
    public void initialize() {
        // 查询出所有黑名单用户，将其放入到布隆过滤器中 TODO 未测试
        securityService.insertBlacklistUserListToBloomFilter(
                securityService.queryAllBlacklistUser()
        );
    }

}
