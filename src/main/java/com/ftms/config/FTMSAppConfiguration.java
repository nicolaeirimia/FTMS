package com.ftms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Spring Application Configuration
 * Main configuration class for FTMS application
 */
@Configuration
@ComponentScan(basePackages = {
    "com.ftms.domain",
    "com.ftms.service"
})
public class FTMSAppConfiguration {
    
    /**
     * Bean Validator for JSR-303/380 validation
     */
    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
