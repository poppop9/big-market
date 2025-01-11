package app.xlog.ggbond;

import app.xlog.ggbond.raffle.model.vo.RetryRouterException;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.executable.ValidateOnExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class TestService {

    @Resource
    private ISecurityRepo securityRepo;

    public void test(@NotBlank String number, @NotNull String s) {
        System.out.println("number = " + number);
        System.out.println("s = " + s);
    }

    @Transactional
    public void testTransaction() {
        securityRepo.updatePassword(200L, "eeeddd");
        throw new RetryRouterException("扣减库存失败，重新调度");
    }

}
