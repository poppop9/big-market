package app.xlog.ggbond.security.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.map.MapBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // todo
        Map<String, List<String>> roleMap = MapBuilder.create(new HashMap<String, List<String>>())
                .put("10001", List.of("admin"))
                .put("10002", List.of("user"))
                .put("10003", List.of("guest"))
                .build();

        // return roleMap.get(loginId.toString());
        return List.of("user");
    }

}