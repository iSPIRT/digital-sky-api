package com.ispirit.digitalsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.configuration.JwtAuthenticationEntryPoint;
import com.ispirit.digitalsky.configuration.JwtAuthenticationFilter;
import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.service.CustomUserDetailService;
import com.ispirit.digitalsky.service.JwtTokenService;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import com.ispirit.digitalsky.service.api.UserService;
import com.ispirit.digitalsky.util.CustomValidator;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Validator;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;

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
