package com.ispirit.digitalsky;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.util.CustomValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
@Configuration
public class TestContext {
    @Bean
    CustomValidator customValidator(Validator validator) {
        return new CustomValidator(validator);
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapperBuilder().build();
    }
}