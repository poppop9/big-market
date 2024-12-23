package app.xlog.ggbond.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

/**
 * 安全领域 - 事件
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SecurityEvents {

    /**
     * 用户登录事件
     */
    @Getter
    public class UserLoginEvent extends ApplicationEvent {
        private final String username;

        public UserLoginEvent(Object source, String username) {
            super(source);
            this.username = username;
        }
    }

}
