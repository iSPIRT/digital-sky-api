package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.service.api.FlightLogService;
import com.ispirit.digitalsky.service.api.FlyDronePermissionApplicationService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.ispirit.digitalsky.AssertionHelper.assertPreAuthorizeWithAdmin;
import static com.ispirit.digitalsky.HandlerMethodHelper.*;
import static com.ispirit.digitalsky.SecurityContextHelper.setUserSecurityContext;
import static com.ispirit.digitalsky.controller.FlyDronePermissionApplicationController.APPLICATION_RESOURCE_BASE_PATH;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@RunWith(SpringRunner.class)
@WebMvcTest(value = FlyDronePermissionApplicationController.class, secure = false)
@Import({TestContext.class})
public class FlyDronePermissionApplicationControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    UserProfileService userProfileService;

    @MockBean
    private OperatorDroneService operatorDroneService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private FlyDronePermissionApplicationService service;

    @MockBean
    private FlightLogService flightLogService;

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = setUserSecurityContext();
    }

    @Test
    public void shouldValidateDroneIdExistBeforeCreatingApplication() throws Exception {

        //given
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(1L);

        when(operatorDroneService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                post(APPLICATION_RESOURCE_BASE_PATH)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        verifyZeroInteractions(service);
    }

    @Test
    public void shouldAuthenticateDroneIdBeforeCreatingApplication() throws Exception {
        //given
        long droneId = 1L;
        long droneOperatorId = 2L;
        long userContextOperatorId = 1L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, userContextOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        //when
        MockHttpServletResponse response = mvc.perform(
                post(APPLICATION_RESOURCE_BASE_PATH)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        verifyZeroInteractions(service);
    }

    @Test
    public void shouldValidateIfCreateApplicationInSubmittedStatus() throws Exception {
        //given
        long droneId = 1L;
        long droneOperatorId = 2L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);
        application.setStatus(ApplicationStatus.SUBMITTED);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        //when
        MockHttpServletResponse response = mvc.perform(
                post(APPLICATION_RESOURCE_BASE_PATH)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        verifyZeroInteractions(service);
    }

    @Test
    public void shouldCreateApplication() throws Exception {
        //given
        long droneId = 1L;
        long droneOperatorId = 2L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        operatorDrone.setOperatorDroneStatus(OperatorDroneStatus.UIN_APPROVED);

        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        //when
        MockHttpServletResponse response = mvc.perform(
                post(APPLICATION_RESOURCE_BASE_PATH)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        verify(service).createApplication(any());
    }

    @Test
    public void shouldValidateDroneIdExistBeforeUpdatingApplication() throws Exception {

        //given
        long applicationId = 1L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(1L);

        when(operatorDroneService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/" + applicationId)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        verifyZeroInteractions(service);
    }

    @Test
    public void shouldAuthenticateDroneIdBeforeUpdatingApplication() throws Exception {
        //given
        long applicationId = 1L;
        long droneId = 1L;
        long droneOperatorId = 2L;
        long userContextOperatorId = 1L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, userContextOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/" + applicationId)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        verifyZeroInteractions(service);
    }

    @Test
    public void shouldCheckForExistingApplicationBeforeUpdate() throws Exception {
        //given
        String applicationId = "1";
        long droneId = 1L;
        long droneOperatorId = 2L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);
        application.setStatus(ApplicationStatus.SUBMITTED);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        operatorDrone.setOperatorDroneStatus(OperatorDroneStatus.UIN_APPROVED);

        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        when(service.get(applicationId)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/" + applicationId)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void shouldCheckIfCurrentUserOwnsApplicationBeforeUpdate() throws Exception {
        //given
        String applicationId = "1";
        long droneId = 1L;
        long droneOperatorId = 2L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);
        application.setStatus(ApplicationStatus.SUBMITTED);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        operatorDrone.setOperatorDroneStatus(OperatorDroneStatus.UIN_APPROVED);

        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        FlyDronePermissionApplication currentApplication = new FlyDronePermissionApplication();
        currentApplication.setApplicantId(2L);
        when(service.get(applicationId)).thenReturn(currentApplication);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/" + applicationId)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }


    @Test
    public void shouldCheckIApplicationCanBeModifiedBeforeUpdate() throws Exception {
        //given
        String applicationId = "1";
        long droneId = 1L;
        long droneOperatorId = 2L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);
        application.setStatus(ApplicationStatus.SUBMITTED);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        operatorDrone.setOperatorDroneStatus(OperatorDroneStatus.UIN_APPROVED);

        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        FlyDronePermissionApplication currentApplication = new FlyDronePermissionApplication();
        currentApplication.setApplicantId(userPrincipal.getId());
        currentApplication.setStatus(ApplicationStatus.SUBMITTED);
        when(service.get(applicationId)).thenReturn(currentApplication);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/" + applicationId)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @Test
    public void shouldValidateApplicationBeforeUpdate() throws Exception {
        //given
        String applicationId = "1";
        long droneId = 1L;
        long droneOperatorId = 2L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);
        application.setStatus(ApplicationStatus.SUBMITTED);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        FlyDronePermissionApplication currentApplication = new FlyDronePermissionApplication();
        currentApplication.setApplicantId(userPrincipal.getId());
        when(service.get(applicationId)).thenReturn(currentApplication);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/" + applicationId)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldUpdateApplication() throws Exception {
        //given
        String applicationId = "1";
        long droneId = 1L;
        long droneOperatorId = 2L;
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setDroneId(droneId);

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        operatorDrone.setOperatorDroneStatus(OperatorDroneStatus.UIN_APPROVED);

        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        FlyDronePermissionApplication currentApplication = new FlyDronePermissionApplication();
        currentApplication.setApplicantId(userPrincipal.getId());
        when(service.get(applicationId)).thenReturn(currentApplication);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/" + applicationId)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        verify(service).updateApplication(eq(applicationId), any());
    }

    @Test
    public void shouldApproveApplication() throws Exception {
        //given
        String applicationId = "1";
        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId(applicationId);
        approveRequestBody.setStatus(ApplicationStatus.SUBMITTED);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(APPLICATION_RESOURCE_BASE_PATH + "/approve/" + applicationId)
                        .content(objectMapper.writeValueAsString(approveRequestBody))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        ArgumentCaptor<ApproveRequestBody> argumentCaptor = ArgumentCaptor.forClass(ApproveRequestBody.class);
        verify(service).approveApplication(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getApplicationFormId(), is(applicationId));
    }

    @Test
    public void shouldValidateDroneIdBeforeGetApplications() throws Exception {
        //given
        when(operatorDroneService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(APPLICATION_RESOURCE_BASE_PATH + "/list")
                        .param("droneId", "1")
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        verifyZeroInteractions(service);
    }

    @Test
    public void shouldAuthenticateDroneIdBeforeGetApplications() throws Exception {
        //given
        long droneId = 1L;
        long droneOperatorId = 2L;
        long userContextOperatorId = 1L;

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, userContextOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(APPLICATION_RESOURCE_BASE_PATH + "/list")
                        .param("droneId", "1")
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        verifyZeroInteractions(service);
    }

    @Test
    public void shouldGetApplicationsForGivenDroneId() throws Exception {
        //given
        long droneId = 1L;
        long droneOperatorId = 2L;

        OperatorDrone operatorDrone = new OperatorDrone(droneOperatorId, ApplicantType.INDIVIDUAL, "", false);
        operatorDrone.setOperatorDroneStatus(OperatorDroneStatus.UIN_APPROVED);

        when(operatorDroneService.find(droneId)).thenReturn(operatorDrone);

        UserProfile userProfile = new UserProfile(userPrincipal.getId(), 0, droneOperatorId, 0, 0, null, null, null);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(userProfile);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(APPLICATION_RESOURCE_BASE_PATH + "/list")
                        .param("droneId", String.valueOf(droneId))
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        verify(service).getApplicationsOfDrone(droneId);
    }

    @Test
    public void shouldGetAllApplications() throws Exception {
        //given

        FlyDronePermissionApplication applicationOne = new FlyDronePermissionApplication();
        applicationOne.setStatus(ApplicationStatus.SUBMITTED);

        FlyDronePermissionApplication applicationTwo = new FlyDronePermissionApplication();
        applicationTwo.setStatus(ApplicationStatus.SUBMITTED);

        FlyDronePermissionApplication applicationThree = new FlyDronePermissionApplication();
        applicationThree.setStatus(ApplicationStatus.SUBMITTED);

        FlyDronePermissionApplication applicationFour = new FlyDronePermissionApplication();
        applicationFour.setStatus(ApplicationStatus.DRAFT);

        when(service.getAllApplications()).thenReturn(asList(applicationOne, applicationTwo, applicationThree, applicationFour));

        //when
        MockHttpServletResponse response = mvc.perform(
                get(APPLICATION_RESOURCE_BASE_PATH + "/getAll")
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        List list = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FlyDronePermissionApplication>>() {
        });
        assertThat(list.size(), is(3));
    }

    @Test
    public void shouldMakeSureOnlyAdminAccess() throws Exception {
        assertPreAuthorizeWithAdmin(patchMethod(mvc, APPLICATION_RESOURCE_BASE_PATH+"/approve/1", MediaType.APPLICATION_JSON));
    }

}