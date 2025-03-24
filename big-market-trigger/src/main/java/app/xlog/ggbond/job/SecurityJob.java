package app.xlog.ggbond.job;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.service.ISecurityService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 安全领域 - 任务
 */
@Slf4j
@Component
public class SecurityJob {

    @Resource
    private ISecurityService securityService;

    /**
     * 查询出所有黑名单用户，将其放入到布隆过滤器中
     */
    @PostConstruct
    public void initializeBlacklistUsers() {
        List<Long> userIds = securityService.queryAllBlacklistUser().stream()
                .map(UserBO::getUserId)
                .toList();
        if (!userIds.isEmpty()) securityService.insertBlacklistUserListToBloomFilter(userIds);
    }

}
