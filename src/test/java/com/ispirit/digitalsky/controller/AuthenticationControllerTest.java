package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.domain.UserRole;
import com.ispirit.digitalsky.dto.TokenRequest;
import com.ispirit.digitalsky.dto.TokenResponse;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AuthenticationController.class, secure = false)
@Import({TestContext.class})
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private SecurityTokenService securityTokenService;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldAuthenticateVerifiedUser() throws Exception {
        //given
        TokenRequest tokenRequest = new TokenRequest("email", "password");
        User user = new User(1, "fullName", "email", "password", emptyList());
        user.setAccountVerified(true);

        UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(
                tokenRequest.getEmail(),
                tokenRequest.getPassword()
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(user),
                tokenRequest.getPassword()
        );

        when(authenticationManager.authenticate(authenticationRequest)).thenReturn(authentication);
        when(securityTokenService.generateToken(authentication)).thenReturn("jwt-token");
        when(userProfileService.profile(1L)).thenReturn(new UserProfile(1, 1, 1, 0, 0, null, null, null));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/auth/token")
                        .content(objectMapper.writeValueAsString(tokenRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(200));
        TokenResponse tokenResponse = objectMapper.readValue(response.getContentAsString(), TokenResponse.class);
        assertThat(tokenResponse.getAccessToken(), is("jwt-token"));
        assertThat(tokenResponse.isAdmin(), is(false));
        assertThat(tokenResponse.getId(), is(1L));
        assertThat(tokenResponse.getPilotProfileId(), is(1L));
        assertThat(tokenResponse.getIndividualOperatorProfileId(), is(1L));
    }

    @Test
    public void shouldAuthenticateAdminUser() throws Exception {
        //given
        TokenRequest tokenRequest = new TokenRequest("email", "password");
        User user = new User(1, "fullName", "email", "password", asList(new UserRole(1,"ROLE_ADMIN")));
        user.setAccountVerified(true);

        UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(
                tokenRequest.getEmail(),
                tokenRequest.getPassword()
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(user),
                tokenRequest.getPassword()
        );

        when(authenticationManager.authenticate(authenticationRequest)).thenReturn(authentication);
        when(securityTokenService.generateToken(authentication)).thenReturn("jwt-token");
        when(userProfileService.profile(1L)).thenReturn(new UserProfile(1, 1, 1, 0, 0, null, null, null));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/auth/token")
                                .content(objectMapper.writeValueAsString(tokenRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(200));
        TokenResponse tokenResponse = objectMapper.readValue(response.getContentAsString(), TokenResponse.class);
        assertThat(tokenResponse.getAccessToken(), is("jwt-token"));
        assertThat(tokenResponse.isAdmin(), is(true));
        assertThat(tokenResponse.getId(), is(1L));
    }

    @Test
    public void shouldNotAuthenticateUnVerifiedUser() throws Exception {
        //given
        TokenRequest tokenRequest = new TokenRequest("email", "password");
        User user = new User(1, "fullName", "email", "password", emptyList());
        user.setAccountVerified(false);

        UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(
                tokenRequest.getEmail(),
                tokenRequest.getPassword()
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(user),
                tokenRequest.getPassword()
        );

        when(authenticationManager.authenticate(authenticationRequest)).thenReturn(authentication);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/auth/token")
                                .content(objectMapper.writeValueAsString(tokenRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        verifyZeroInteractions(securityTokenService);
        verifyZeroInteractions(userProfileService);
    }


    @Test
    public void shouldNotAuthenticateOnAuthException() throws Exception {
        //given
        TokenRequest tokenRequest = new TokenRequest("email", "password");
        User user = new User(1, "fullName", "email", "password", emptyList());
        user.setAccountVerified(false);

        UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(
                tokenRequest.getEmail(),
                tokenRequest.getPassword()
        );

        when(authenticationManager.authenticate(authenticationRequest)).thenThrow(new BadCredentialsException(""));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/auth/token")
                                .content(objectMapper.writeValueAsString(tokenRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        verifyZeroInteractions(securityTokenService);
        verifyZeroInteractions(userProfileService);
    }

}