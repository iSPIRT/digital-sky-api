package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.BasicApplication;
import com.ispirit.digitalsky.document.ImportDroneApplication;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplication;
import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.repository.UserRepository;
import com.ispirit.digitalsky.service.api.DroneAcquisitionApplicationService;
import com.ispirit.digitalsky.service.api.EmailService;
import com.ispirit.digitalsky.service.api.UAOPApplicationService;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomUserDetailServiceTest {


    private UserRepository userRepository;
    private EmailService emailService;
    private DroneAcquisitionApplicationService localDroneService;
    private DroneAcquisitionApplicationService importDroneService;
    private UAOPApplicationService uaopApplicationService;
    private CustomUserDetailService service;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        localDroneService = mock(DroneAcquisitionApplicationService.class);
        importDroneService = mock(DroneAcquisitionApplicationService.class);
        uaopApplicationService = mock(UAOPApplicationService.class);
        service = new CustomUserDetailService(
                userRepository,
                emailService,
                localDroneService,
                importDroneService,
                uaopApplicationService, "/resetPassword", "/accountVerify");
    }

    @Test
    public void shouldLoadUserByName() throws Exception {
        //given
        String email = "test@email.com";
        UserRole userRole = new UserRole(1, "ROLE_USER");
        User user = new User(1, "name", email, null, asList(userRole));
        when(userRepository.loadByEmail(email)).thenReturn(user);

        //when
        UserPrincipal userPrincipal = (UserPrincipal) service.loadUserByUsername(email);

        //then
        assertThat(userPrincipal.getEmail(), is(user.getEmail()));
        assertThat(userPrincipal.getUsername(), is(user.getFullName()));
        assertThat(userPrincipal.getId(), is(user.getId()));
        assertThat(userPrincipal.getAuthorities().size(), is(1));
        assertThat(userPrincipal.getAuthorities().iterator().next().getAuthority(), is(userRole.getUserRole()));
    }

    @Test
    public void shouldFindUserById() throws Exception {
        //given
        User user = new User(1, "name", "email", null, emptyList());
        when(userRepository.findOne(1L)).thenReturn(user);

        //when
        User entity = service.findUserById(1L);

        asList(entity, is(user));
    }

    @Test
    public void shouldFindById() throws Exception {
        //given
        User user = new User(1, "name", "email", null, emptyList());
        when(userRepository.findOne(1L)).thenReturn(user);

        //when
        User entity = service.find(1L);

        asList(entity, is(user));
    }

    @Test
    public void shouldGenerateResetPasswordLink() throws Exception {
        //given
        String email = "test@email.com";
        User user = new User(1, "name", "email", null, emptyList());
        when(userRepository.loadByEmail(email)).thenReturn(user);


        //when
        service.generateResetPasswordLink(email);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().getResetPasswordToken(), notNullValue());

        ArgumentCaptor<ResetPasswordEmail> emailArgumentCaptor = ArgumentCaptor.forClass(ResetPasswordEmail.class);
        verify(emailService).send(emailArgumentCaptor.capture());

        String resetPasswordLink = format("/resetPassword?token=%s", userArgumentCaptor.getValue().getResetPasswordToken());
        assertThat(emailArgumentCaptor.getValue().templateParameters().get("resetPasswordLink"), is(resetPasswordLink));

    }

    @Test
    public void shouldThrowExceptionWhenUserNotFoundForResetPasswordLink() throws Exception {
        //given
        String email = "test@email.com";
        when(userRepository.loadByEmail(email)).thenReturn(null);


        try {
            service.generateResetPasswordLink(email);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {

        }
    }

    @Test
    public void shouldSendAccountVerificationLink() throws Exception {
        //given
        User user = new User(1, "name", "email", null, emptyList());
        when(userRepository.findOne(user.getId())).thenReturn(user);


        //when
        service.sendEmailVerificationLink(user);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().getAccountVerificationToken(), notNullValue());

        ArgumentCaptor<AccountVerificationEmail> emailArgumentCaptor = ArgumentCaptor.forClass(AccountVerificationEmail.class);
        verify(emailService).send(emailArgumentCaptor.capture());

        String resetPasswordLink = format("/accountVerify?token=%s", userArgumentCaptor.getValue().getAccountVerificationToken());
        assertThat(emailArgumentCaptor.getValue().templateParameters().get("accountVerificationLink"), is(resetPasswordLink));

    }

    @Test
    public void shouldResetPassword() throws Exception {
        //given
        String newPasswordHash = "newPasswordHash";
        String token = "token";
        User user = new User(1, "name", "email", null, emptyList());
        when(userRepository.loadByResetPasswordToken(token)).thenReturn(user);

        //when
        service.resetPassword(token, newPasswordHash);

        //then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getResetPasswordToken(), nullValue());
        assertThat(argumentCaptor.getValue().getPassword(), is(newPasswordHash));
    }

    @Test
    public void shouldThrowExceptionIfUserNotFoundForResetPassword() throws Exception {
        //given
        String newPasswordHash = "newPasswordHash";
        String token = "token";
        when(userRepository.loadByResetPasswordToken(token)).thenReturn(null);

        try {
            service.resetPassword(token, newPasswordHash);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
        }
    }

    @Test
    public void shouldVerifyAccount() throws Exception {
        //given
        String token = "token";
        User user = new User(1, "name", "email", null, emptyList());
        when(userRepository.loadByAccountVerificationToken(token)).thenReturn(user);

        //when
        service.verifyAccount(token);

        //then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getAccountVerificationToken(), nullValue());
        assertThat(argumentCaptor.getValue().isAccountVerified(), is(true));
    }

    @Test
    public void shouldThrowExceptionIfUserNotFoundForAccountVerification() throws Exception {
        //given
        String token = "token";
        when(userRepository.loadByResetPasswordToken(token)).thenReturn(null);

        try {
            service.verifyAccount(token);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
        }
    }

    @Test
    public void shouldThrowExceptionIfUserAlreadyVerified() throws Exception {
        //given
        String token = "token";
        User user = new User(1, "name", "email", null, emptyList());
        user.setAccountVerified(true);
        when(userRepository.loadByResetPasswordToken(token)).thenReturn(user);

        try {
            service.verifyAccount(token);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
        }
    }

    @Test
    public void shouldLoadUserByEmail() throws Exception {
        //given
        String email = "email";

        //when
        service.loadByEmail(email);

        //then
        verify(userRepository).loadByEmail(email);
    }

    @Test
    public void shouldCreateUser() throws Exception {
        //given
        User user = new User(1, "name", "email", null, emptyList());

        //when
        service.createNew(user);

        //then
        verify(userRepository).save(user);
    }

    @Test
    public void shouldLoadAllUserApplications() throws Exception {
        //given
        long id = 1L;
        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setLastModifiedDate(new Date(System.currentTimeMillis()));
        when(uaopApplicationService.getApplicationsOfApplicant(1L)).thenReturn(asList(uaopApplication));

        LocalDroneAcquisitionApplication localDroneAcquisitionApplication = new LocalDroneAcquisitionApplication();
        localDroneAcquisitionApplication.setLastModifiedDate(new Date(System.currentTimeMillis() - 1000));
        when(localDroneService.getApplicationsOfApplicant()).thenReturn(asList(localDroneAcquisitionApplication));

        ImportDroneApplication importDroneApplication = new ImportDroneApplication();
        importDroneApplication.setLastModifiedDate(new Date(System.currentTimeMillis() - 2000));
        when(importDroneService.getApplicationsOfApplicant()).thenReturn(asList(importDroneApplication));

        //when
        List<BasicApplication> applications = service.applications(id);

        assertThat(applications.size(), is(3));
        assertThat(applications.get(0), is(uaopApplication));
        assertThat(applications.get(1), is(localDroneAcquisitionApplication));
        assertThat(applications.get(2), is(importDroneApplication));
    }
}