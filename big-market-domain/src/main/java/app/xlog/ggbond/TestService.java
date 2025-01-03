package app.xlog.ggbond;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.executable.ValidateOnExecution;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class TestService {

    public void test(@NotBlank String number, @NotNull String s) {
        System.out.println("number = " + number);
        System.out.println("s = " + s);
    }

}
