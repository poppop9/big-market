package app.xlog.ggbond;

import app.xlog.ggbond.exception.RetryRouterException;
import app.xlog.ggbond.resp.BigMarketRespCode;
import app.xlog.ggbond.security.repository.ISecurityRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class TestService {

    @Resource
    private ISecurityRepo securityRepo;

    public void test( String number, String s) {
        System.out.println("number = " + number);
        System.out.println("s = " + s);
    }

    @Transactional
    public void testTransaction() {
        securityRepo.updatePassword(200L, "eeeddd");
        throw new RetryRouterException(BigMarketRespCode.DECREASE_AWARD_COUNT_FAILED, "扣减库存失败，重新调度");
    }

}
