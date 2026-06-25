package dev.aditya.userauthservice.Configuration;

import dev.aditya.userauthservice.Validation.ControllerValidator;
import dev.aditya.userauthservice.Validation.ServiceValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

    @Bean
    public ServiceValidator generateNewServiceValidator()
    {
        return new ServiceValidator();
    }

    @Bean
    public ControllerValidator generateControllerValidator(){return new ControllerValidator();}
}
