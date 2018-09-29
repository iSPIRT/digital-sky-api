package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.service.api.OccurrenceReportService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(value = OccurrenceReportController.class, secure = false)
@Import({TestContext.class})
public class OccurrenceReportControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OccurrenceReportService occurrenceReportService;

    @MockBean
    private OperatorDroneService operatorDroneService;

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = SecurityContextHelper.setUserSecurityContext();
    }

    @Test
    public void shouldValidateDroneIdBeforeCreatingReport() throws Exception {
        //given
        OccurrenceReport occurrenceReport = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");

        when(operatorDroneService.find(occurrenceReport.getOperatorDroneId())).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/occurrenceReport")
                                .content(objectMapper.writeValueAsString(occurrenceReport))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));

    }

    @Test
    public void shouldNotAllowUnAuthorizedUserToCreateReport() throws Exception {
        //given
        OccurrenceReport occurrenceReport = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");

        OperatorDrone operatorDrone = new OperatorDrone();
        operatorDrone.setOperatorId(2L);
        operatorDrone.setOperatorType(ApplicantType.ORGANISATION);
        when(operatorDroneService.find(occurrenceReport.getOperatorDroneId())).thenReturn(operatorDrone);

        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 0, 0, 3L, 0));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/occurrenceReport")
                                .content(objectMapper.writeValueAsString(occurrenceReport))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));

    }

    @Test
    public void shouldCreateReport() throws Exception {
        //given
        OccurrenceReport occurrenceReport = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");

        OperatorDrone operatorDrone = new OperatorDrone();
        operatorDrone.setOperatorId(2L);
        operatorDrone.setOperatorType(ApplicantType.ORGANISATION);
        when(operatorDroneService.find(occurrenceReport.getOperatorDroneId())).thenReturn(operatorDrone);

        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 0, 0, 2L, 0));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/occurrenceReport")
                                .content(objectMapper.writeValueAsString(occurrenceReport))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        ArgumentCaptor<OccurrenceReport> argumentCaptor = ArgumentCaptor.forClass(OccurrenceReport.class);
        verify(occurrenceReportService).createNew(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCreatedById(), is(userPrincipal.getId()));

    }

    @Test
    public void shouldGetReportById() throws Exception {
        //given
        OccurrenceReport occurrenceReport = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");

        OperatorDrone operatorDrone = new OperatorDrone();
        operatorDrone.setOperatorId(2L);
        operatorDrone.setOperatorType(ApplicantType.ORGANISATION);
        when(operatorDroneService.find(occurrenceReport.getOperatorDroneId())).thenReturn(operatorDrone);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 0, 0, 2L, 0));

        when(occurrenceReportService.find(1L)).thenReturn(occurrenceReport);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/occurrenceReport/1")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        OccurrenceReport responseEntity = objectMapper.readValue(response.getContentAsString(), OccurrenceReport.class);
        assertThat(responseEntity.getOperatorDroneId(), is(occurrenceReport.getOperatorDroneId()));
    }

    @Test
    public void shouldNotGetReportIfNotFound() throws Exception {

        when(occurrenceReportService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/occurrenceReport/1")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void shouldNotGetReportWhenUnAuthorizedAccess() throws Exception {
        //given
        OccurrenceReport occurrenceReport = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");

        OperatorDrone operatorDrone = new OperatorDrone();
        operatorDrone.setOperatorId(2L);
        operatorDrone.setOperatorType(ApplicantType.ORGANISATION);
        when(operatorDroneService.find(occurrenceReport.getOperatorDroneId())).thenReturn(operatorDrone);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 0, 0, 3L, 0));

        when(occurrenceReportService.find(1L)).thenReturn(occurrenceReport);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/occurrenceReport/1")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void shouldGetAllReportForDrone() throws Exception {
        //given
        OccurrenceReport occurrenceReportOne = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");
        OccurrenceReport occurrenceReportTwo = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");

        OperatorDrone operatorDrone = new OperatorDrone();
        operatorDrone.setOperatorId(2L);
        operatorDrone.setOperatorType(ApplicantType.ORGANISATION);
        when(operatorDroneService.find(1L)).thenReturn(operatorDrone);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 0, 0, 2L, 0));

        when(occurrenceReportService.findByDroneId(1L)).thenReturn(asList(occurrenceReportOne, occurrenceReportTwo));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/occurrenceReport/drone/1/list")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        List<OccurrenceReport> reports = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<OccurrenceReport>>(){});
        assertThat(reports.size(), is(2));
    }

    @Test
    public void shouldNotGetAllReportIfDroneIdNotFound() throws Exception {
        //given
        when(operatorDroneService.find(1L)).thenReturn(null);


        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/occurrenceReport/drone/1/list")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldNotGetAllReportForDroneWhenUnAUthorizedAccess() throws Exception {
        //given
        OccurrenceReport occurrenceReportOne = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");
        OccurrenceReport occurrenceReportTwo = new OccurrenceReport(2L, LocalDateTime.now(),"bng","12","12","t","r","r","rpad","pd","di","pilotd","uaop","od","da","dh","pd");

        OperatorDrone operatorDrone = new OperatorDrone();
        operatorDrone.setOperatorId(2L);
        operatorDrone.setOperatorType(ApplicantType.ORGANISATION);
        when(operatorDroneService.find(1L)).thenReturn(operatorDrone);
        when(userProfileService.profile(userPrincipal.getId())).thenReturn(new UserProfile(userPrincipal.getId(), 0, 0, 3L, 0));

        when(occurrenceReportService.findByDroneId(1L)).thenReturn(asList(occurrenceReportOne, occurrenceReportTwo));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/occurrenceReport/drone/1/list")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }
}