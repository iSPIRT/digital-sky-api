package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.InputStream;

import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtTokenServiceTest {

    private ResourceLoader resourceLoader;
    private JwtTokenService service;

    @Before
    public void setUp() throws Exception {
        resourceLoader = mock(ResourceLoader.class);

        InputStream keyStoreStream = this.getClass().getResourceAsStream("/keystore.jks");
        Resource keystoreResource = mock(Resource.class);
        when(resourceLoader.getResource("keystore.jks")).thenReturn(keystoreResource);
        when(keystoreResource.getInputStream()).thenReturn(keyStoreStream);

        service = new JwtTokenService(resourceLoader,2,"keystore.jks","cacms789","jks","tomcat-localhost","cacms789");
    }

    @Test
    public void shouldGenerateJwtToken() throws Exception {
        //given
        User user = new User(1,"name","email","", emptyList());
        String token = service.generateToken(new UsernamePasswordAuthenticationToken(new UserPrincipal(user), null));

        //when
        boolean isValidToken = service.validateToken(token);

        //then
        assertThat(isValidToken, is(true));

    }

    @Test
    public void shouldGenerateIdFromJwtToken() throws Exception {
        //given
        User user = new User(1,"name","email","", emptyList());
        String token = service.generateToken(new UsernamePasswordAuthenticationToken(new UserPrincipal(user), null));

        //when
        String id = service.getUserIdFromJWT(token);

        //then
        assertThat(id, is(String.valueOf(user.getId())));

    }
}