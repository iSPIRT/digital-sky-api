package com.ispirit.digitalsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.util.CustomValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Validator;

@Configuration
public class TestContext {
    public static final String OBJECT_MAPPER = "object.mapper";

    @Bean
    CustomValidator customValidator(Validator validator) {
        return new CustomValidator(validator);
    }

    @Bean(OBJECT_MAPPER)
    public ObjectMapper objectMapper() {
        return new ObjectMapperBuilder().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

