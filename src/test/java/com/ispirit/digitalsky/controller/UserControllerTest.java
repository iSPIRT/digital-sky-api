package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.document.BasicApplication;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplication;
import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.dto.AccountVerificationRequest;
import com.ispirit.digitalsky.dto.ResetPasswordLinkRequest;
import com.ispirit.digitalsky.dto.ResetPasswordRequest;
import com.ispirit.digitalsky.dto.TokenResponse;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.service.api.ReCaptchaService;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import com.ispirit.digitalsky.service.api.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class, secure = false)
@Import({TestContext.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ReCaptchaService reCaptchaService;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private SecurityTokenService securityTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = SecurityContextHelper.setUserSecurityContext();
    }

    @Test
    public void shouldVerifyCaptchaBeforeCreate() throws Exception {
        //given

        String userPayload = "{\"id\":0,\"fullName\":\"fullName\",\"email\":\"test@email.com\",\"password\":\"password\",\"reCaptcha\":\"captcha\"}";
        when(userService.createNew(any(User.class))).thenReturn(new User("fullName", "test@email.com", "password"));

        //when
        mvc.perform(
                post("/api/user")
                        .content(userPayload)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        verify(reCaptchaService).verifyCaptcha("captcha");
    }

    @Test
    public void shouldVerifyExistingUserBeforeCreate() throws Exception {
        //given

        String userPayload = "{\"id\":0,\"fullName\":\"fullName2\",\"email\":\"test@email.com\",\"password\":\"password\",\"reCaptcha\":\"captcha\"}";
        when(userService.loadByEmail("test@email.com")).thenReturn(new User("fullName", "test@email.com", "password"));

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user")
                        .content(userPayload)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.CONFLICT.value()));
        verify(userService, never()).createNew(any());
    }

    @Test
    public void shouldCreateUser() throws Exception {
        //given

        String userPayload = "{\"id\":0,\"fullName\":\"fullName2\",\"email\":\"test@email.com\",\"password\":\"password\",\"reCaptcha\":\"captcha\"}";
        when(userService.createNew(any(User.class))).thenReturn(new User("fullName", "test@email.com", "password"));

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user")
                        .content(userPayload)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        verify(userService).createNew(any(User.class));
        verify(userService).sendEmailVerificationLink(any(User.class));
    }

    @Test
    public void shouldGetUser() throws Exception {
        //given
        User user = new User("fullName", "test@email.com", "password");
        when(userService.find(userPrincipal.getId())).thenReturn(user);

        //when
        MockHttpServletResponse response = mvc.perform(
                get("/api/user/" + userPrincipal.getId())
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        User result = objectMapper.readValue(response.getContentAsString(), User.class);
        assertThat(result.getEmail(), is(user.getEmail()));
        assertThat(result.getFullName(), is(user.getFullName()));
        assertThat(result.getPassword(), nullValue());
    }

    @Test
    public void shouldNotAllowOtherUsersUserDetails() throws Exception {
        //given
        User user = new User("fullName", "test@email.com", "password");
        when(userService.find(userPrincipal.getId())).thenReturn(user);

        //when
        MockHttpServletResponse response = mvc.perform(
                get("/api/user/" + userPrincipal.getId() + 1)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void shouldAllowAdminToGetUser() throws Exception {
        //given
        userPrincipal = SecurityContextHelper.setAdminUserSecurityContext();
        User user = new User("fullName", "test@email.com", "password");
        when(userService.find(userPrincipal.getId())).thenReturn(user);

        //when
        MockHttpServletResponse response = mvc.perform(
                get("/api/user/" + userPrincipal.getId())
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldGetAllApplicationsOfUser() throws Exception {
        //given
        List<BasicApplication> applications = asList(new UAOPApplication(), new LocalDroneAcquisitionApplication());
        when(userService.applications(userPrincipal.getId())).thenReturn(applications);

        //when
        MockHttpServletResponse response = mvc.perform(
                get("/api/user/applications")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        List responseApplications = objectMapper.readValue(response.getContentAsString(), new TypeReference<List>() {
        });

        assertThat(responseApplications.size(), is(applications.size()));
    }

    @Test
    public void shouldGenerateResetPasswordLink() throws Exception {
        //given
        ResetPasswordLinkRequest resetPasswordLinkRequest = new ResetPasswordLinkRequest("test@email.com");

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user/resetPasswordLink")
                        .content(objectMapper.writeValueAsString(resetPasswordLinkRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        verify(userService).generateResetPasswordLink(resetPasswordLinkRequest.getEmail());
    }

    @Test
    public void shouldHandleInvalidEmailForResetPasswordLink() throws Exception {
        //given
        ResetPasswordLinkRequest resetPasswordLinkRequest = new ResetPasswordLinkRequest("test@email.com");
        doThrow(EntityNotFoundException.class).when(userService).generateResetPasswordLink(resetPasswordLinkRequest.getEmail());

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user/resetPasswordLink")
                        .content(objectMapper.writeValueAsString(resetPasswordLinkRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void resetPasswordShouldReturnTokenResponseOnSuccess() throws Exception {
        //given
        ResetPasswordRequest request = new ResetPasswordRequest("token", "newPassword");
        User user = new User(userPrincipal.getId(), "fullName", "test@email.com", "password", emptyList());
        when(userService.resetPassword(eq(request.getToken()), anyString())).thenReturn(user);
        when(securityTokenService.generateToken(any())).thenReturn("accessToken");
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 1, 0, 0, 0, "123", "", ""));

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user/resetPassword")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        TokenResponse tokenResponse = objectMapper.readValue(response.getContentAsString(), TokenResponse.class);
        assertThat(tokenResponse.getAccessToken(), is("accessToken"));
    }

    @Test
    public void shouldHandleInvalidTokenForResetPassword() throws Exception {
        //given
        ResetPasswordRequest request = new ResetPasswordRequest("token", "newPassword");
        when(userService.resetPassword(eq(request.getToken()), anyString())).thenThrow(EntityNotFoundException.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user/resetPassword")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void verifyUserShouldReturnTokenResponseOnSuccess() throws Exception {
        //given
        AccountVerificationRequest request = new AccountVerificationRequest("token");
        User user = new User(userPrincipal.getId(), "fullName", "test@email.com", "password", emptyList());
        when(userService.verifyAccount(eq(request.getToken()))).thenReturn(user);
        when(securityTokenService.generateToken(any())).thenReturn("accessToken");
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 1, 0, 0, 0, "123", "", ""));

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user/verify")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        TokenResponse tokenResponse = objectMapper.readValue(response.getContentAsString(), TokenResponse.class);
        assertThat(tokenResponse.getAccessToken(), is("accessToken"));
    }

    @Test
    public void shouldHandleInvalidTokenForVerifyAccount() throws Exception {
        //given
        AccountVerificationRequest request = new AccountVerificationRequest("token");
        when(userService.verifyAccount(eq(request.getToken()))).thenThrow(EntityNotFoundException.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                post("/api/user/verify")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }
}