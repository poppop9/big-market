package app.xlog.ggbond.security.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * 安全领域 - 权限和角色的配置
 */
@Configuration
public class SaTokenPermissionConfig implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    /**
     * 返回一个账号所拥有的角色集合
     *
     * @param loginId   用户id
     * @param loginType 账号体系标识
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of(
                StpUtil.getSession().get("role").toString()
        );
    }

}