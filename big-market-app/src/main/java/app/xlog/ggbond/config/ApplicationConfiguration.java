package app.xlog.ggbond.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ApplicationConfiguration {
	@Bean
	public static MethodValidationPostProcessor validationPostProcessor() {
		MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
		processor.setAdaptConstraintViolations(true);
		return processor;
	}
}