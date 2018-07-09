package com.ispirit.digitalsky.configuration;

import com.ispirit.digitalsky.repository.UserRepository;
import com.ispirit.digitalsky.service.CustomUserDetailService;
import com.ispirit.digitalsky.service.SendGridEmailService;
import com.ispirit.digitalsky.service.api.EmailService;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import com.ispirit.digitalsky.service.JwtTokenService;
import com.ispirit.digitalsky.service.api.UserService;
import com.sendgrid.SendGrid;
import freemarker.template.TemplateExceptionHandler;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfiguration {

    @Value("${server.port}")
    private int port;

    @Value("${server.http-port}")
    private int httpPort;

    @Value("${DEFAULT_FROM_EMAIL_ID:no-reply@digitalsky.com}")
    private String defaultFromEmailId;

    @Value("${SEND_GRID_API_KEY:default}")
    private String sendGridApiKey;

    @Value("${RESET_PASSWORD_PATH:http://localhost:3000/resetPassword}")
    private String resetPasswordBasePath;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService;
    }

    @Bean
    public UserService userService(UserRepository userRepository, EmailService emailService) {
        return new CustomUserDetailService(userRepository, emailService, resetPasswordBasePath);
    }

    @Bean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public SecurityTokenService securityTokenService() {
        return new JwtTokenService(1000000);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(SecurityTokenService securityTokenService, UserService userDetailsService) {
        return new JwtAuthenticationFilter(userDetailsService, securityTokenService);
    }

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_20);
        cfg.setClassForTemplateLoading(this.getClass(), "/emailTemplates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocalizedLookup(false);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }

    @Bean
    EmailService sendGridEmailService(freemarker.template.Configuration freemarkerConfiguration) {
        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        return new SendGridEmailService(sendGrid, freemarkerConfiguration, defaultFromEmailId);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(getHttpConnector());
        return tomcat;
    }

    private Connector getHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(port);
        return connector;
    }

}
