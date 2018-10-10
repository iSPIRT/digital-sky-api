package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ValidationException;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UserProfileService;
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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(value = OperatorDroneController.class, secure = false)
@Import({TestContext.class})
public class OperatorDroneControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    OperatorDroneService operatorDroneService;

    @MockBean
    UserProfileService userProfileService;

    @Autowired
    ObjectMapper mapper;

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() {
        userPrincipal = SecurityContextHelper.setUserSecurityContext();
    }

    @Test
    public void shouldListDronesForTheOperator() throws Exception {

       OperatorDrone opDrone = new OperatorDrone(1L, ApplicantType.ORGANISATION,null, false);
        CollectionType javaType = mapper.getTypeFactory()
                .constructCollectionType(List.class, OperatorDrone.class);

       //given
       doReturn(asList(opDrone)).when(operatorDroneService).loadByOperator();

       //when
        MockHttpServletResponse response  =
                mvc.perform(get("/api/operatorDrone")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

       //then
        verify(operatorDroneService).loadByOperator();
        List<OperatorDrone> result = mapper.readValue(response.getContentAsString(), javaType);

        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.get(0), instanceOf(OperatorDrone.class));
        assertThat(result.get(0).getOperatorId(), is(1L));
    }

    @Test
    public void shouldHandleValidationExceptionForListDrones() throws Exception {
        //given
        when(operatorDroneService.loadByOperator()).thenThrow(new ValidationException(new Errors("Applicant not operator")));

        //when
        MockHttpServletResponse response  =
                mvc.perform(get("/api/operatorDrone")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        //then
        verify(operatorDroneService).loadByOperator();
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));

        Errors responseError = mapper.readValue(response.getContentAsString(), Errors.class);
        assertThat(responseError.getErrors().get(0), is("Applicant not operator"));
    }

    @Test
    public void shouldHandleDroneNotFoundExceptionWhileGettingOperatorDrone() throws Exception {
        //given
        when(operatorDroneService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response  =
                mvc.perform(get("/api/operatorDrone/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        //then
        verify(operatorDroneService).find(eq(1L));
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        Errors responseError = mapper.readValue(response.getContentAsString(), Errors.class);
        assertThat(responseError.getErrors().get(0), is("Drone not found"));

        verifyZeroInteractions(userProfileService);

    }

    @Test
    public void shouldHandleUnAuthorizedAccessExceptionWhileGettingOperatorDrone() throws Exception {
        UserProfile mockProfile = mock(UserProfile.class);
        OperatorDrone opDrone = new OperatorDrone(1L, ApplicantType.ORGANISATION,null, false);

        //given
        when(operatorDroneService.find(1L)).thenReturn(opDrone);
        when(userProfileService.profile(1L)).thenReturn(mockProfile);
        doReturn(false).when(mockProfile).owns(opDrone);

        //when
        MockHttpServletResponse response  =
                mvc.perform(get("/api/operatorDrone/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        //then
        verify(mockProfile).owns(opDrone);
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        Errors responseError = mapper.readValue(response.getContentAsString(), Errors.class);
        assertThat(responseError.getErrors().get(0), is("UnAuthorized Access"));
    }

    @Test
    public void shouldGetOperatorDrone() throws Exception {
        UserProfile mockProfile = mock(UserProfile.class);
        OperatorDrone opDrone = new OperatorDrone(1L, ApplicantType.ORGANISATION,null, false);
        opDrone.setDeviceId("ef7899999");

        //given
        when(operatorDroneService.find(1L)).thenReturn(opDrone);
        when(userProfileService.profile(1L)).thenReturn(mockProfile);
        doReturn(true).when(mockProfile).owns(opDrone);

        //when
        MockHttpServletResponse response  =
                mvc.perform(get("/api/operatorDrone/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        //then
        verify(mockProfile).owns(opDrone);
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        OperatorDrone operatorDrone = mapper.readValue(response.getContentAsString() , OperatorDrone.class);
        assertThat(operatorDrone.getDeviceId(), is("ef7899999"));
    }

}
