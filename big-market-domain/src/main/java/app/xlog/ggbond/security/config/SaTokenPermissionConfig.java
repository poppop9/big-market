package app.xlog.ggbond.security.config;

import app.xlog.ggbond.security.model.UserBO;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.map.MapBuilder;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全领域 - 权限和角色的配置
 */
@Configuration
public class SaTokenPermissionConfig implements StpInterface {

    @Resource
    private ISecurityRepo securityRepo;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    /**
     * 返回一个账号所拥有的角色集合
     *
     * @param loginId   用户id
     * @param loginType 账号体系标识
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        UserBO user = securityRepo.findByUserId(Long.valueOf(loginId.toString()));
        return List.of(user.getUserRole().name());
    }

}